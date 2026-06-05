# Dockerfile Deep-Dive Explanation

This document explains the multi-stage `Dockerfile` used in the Travery-Backend project. The architecture of this Dockerfile represents the industry-standard **"Multi-Stage Build"** pattern combined with **"Spring Boot Layering"**. It is specifically optimized to ensure maximum CI/CD performance, top-tier security, and minimal storage size.

---

## 1. Concept: Docker Layers & Caching Optimization
Before analyzing the file line-by-line, it is crucial to understand how Docker processes instructions:
*   **The Layer Cake:** Every `RUN`, `COPY`, or `ADD` command in a Dockerfile creates a new, immutable "layer" stacked on top of the previous one.
*   **The Caching Rule:** Docker caches each layer. When rebuilding an image, Docker checks if the instruction and the files it references have changed. If *not*, it reuses the cached layer. **However, if a layer is invalidated (because files changed), ALL subsequent layers below it are also invalidated and must be rebuilt.**
*   **The Optimization Strategy:** To achieve lightning-fast builds, we must place instructions that change rarely (like downloading internet libraries) at the TOP of the file, and instructions that change frequently (like compiling our own source code) at the BOTTOM. You will see this strategy applied rigorously in both stages below.

---

## 2. STAGE 1: BUILD (The Compilation Phase)
In this stage, the goal is to compile the raw Java source code into an executable `.jar` file and extract its internal layers.

*   `FROM maven:3.9-eclipse-temurin-25 AS builder`
    *   Defines the base image for compilation. We use an image containing the Maven build tool and the full Java 25 Development Kit (JDK).
    *   `AS builder`: Names this temporary stage "builder" so we can reference it later to grab files from it.
    *   > **Where do these Base Images come from?**
        > Base images are sourced from **Docker Hub** (`https://hub.docker.com/`), the official public registry for container images. When choosing a base image to start a project, you search Docker Hub for the "Official Image" badge (e.g., searching for `maven`, `eclipse-temurin`, or `postgres`). The platform lists all available tags (versions), allowing you to pick the exact OS and software version your project requires.

*   `WORKDIR /app`
    *   Sets the working directory inside the container to `/app`. All subsequent commands will be executed here.

*   `COPY pom.xml ./`
    *   Copies ONLY the `pom.xml` file from your laptop/server into the container. 
    *   *Why not copy the source code yet?* To protect the cache. Source code changes every day; `pom.xml` changes rarely.

*   `RUN mvn dependency:resolve -B`
    *   Downloads all Maven dependencies (Spring, Hibernate, etc.) listed in the `pom.xml`. 
    *   Because this is done *before* copying the Java source code, Docker caches this massive download step. If you only change a `.java` file, Docker completely skips downloading the internet dependencies on the next run, saving 2-3 minutes of build time.
    *   **Flag `-B` (`--batch-mode`)**: This tells Maven to run in non-interactive mode. By default, Maven prints thousands of lines of ASCII progress bars when downloading libraries. `-B` disables this, keeping the Docker and CI/CD logs clean, readable, and preventing log buffer overflows.

*   `COPY src ./src`
    *   Now, copy the actual Java source code into the container.

*   `RUN mvn clean package -DskipTests -B`
    *   Compiles the code and packages it into a `.jar` file located in the `target/` directory.
    *   **Flag `-DskipTests`**: Instructs Maven to completely bypass compiling and running unit/integration tests. *Why?* Because in a proper CI/CD workflow, tests have already been strictly verified in a previous dedicated GitHub Actions job. Re-running them inside the Docker build is redundant and wastes compute time.
    *   **Flag `-B` (`--batch-mode`)**: Again, ensures the compilation log output remains clean by suppressing interactive prompts and download progress bars.

*   `RUN java -Djarmode=layertools -jar target/*.jar extract`
    *   **The Spring Boot Magic**: A standard Spring Boot "Fat Jar" contains both your tiny code and ~100MB of third-party libraries bundled together. If we just copy the `.jar` directly into an image, any small code change (like fixing a typo) invalidates the entire 100MB layer, forcing a massive upload during deployment.
    *   This command mathematically extracts the `.jar` into **4 separate component folders**:
        1.  **`dependencies`**: Contains regular third-party libraries (e.g., Spring framework, PostgreSQL driver). *Changes very rarely.*
        2.  **`spring-boot-loader`**: The core Spring classes required to boot the application. *Changes almost never.*
        3.  **`snapshot-dependencies`**: Third-party libraries that are still under development (versions ending in `-SNAPSHOT`). *Changes occasionally.*
        4.  **`application`**: The actual compiled `.class` files of our Travery-Backend project and `application.yml`. *Changes every single time we push a commit.*

---

## 3. STAGE 2: RUNTIME (The Production Phase)
In this stage, we discard all the heavy compilation tools (Maven, JDK, raw source code) and only keep the absolute bare minimum needed to run the app on the Production Server.

*   `FROM eclipse-temurin:25-jre AS runtime`
    *   Defines the runtime image. Notice we use `jre` (Java Runtime Environment) instead of JDK. This drastically reduces the final image size (from ~400MB down to ~150MB) and minimizes the security attack surface.

*   `RUN groupadd --system appgroup && useradd --system --gid appgroup appuser`
    *   **Deep Dive: Principle of Least Privilege**
        *   **The Threat:** By default, Docker runs applications as the `root` user (UID 0) inside the container. If a hacker exploits a vulnerability in your Java app (e.g., an unsafe file upload, or a vulnerability like Log4Shell leading to Remote Code Execution), they inherit the permissions of the application. If the app is `root`, the hacker can install malware, alter critical system binaries, or attempt a "Container Breakout" to attack the underlying physical Server.
        *   **The Solution:** This command creates a restricted, non-root user (`appuser`) and group (`appgroup`) with zero administrative privileges. We strip the application of its `root` powers.

*   `WORKDIR /app`
    *   Sets the working directory for the final runtime container.

*   `COPY --from=builder /app/dependencies/ ./`
*   `COPY --from=builder /app/spring-boot-loader/ ./`
*   `COPY --from=builder /app/snapshot-dependencies/ ./`
*   `COPY --from=builder /app/application/ ./`
    *   **Applying Layer Optimization**: We copy the 4 extracted components from the `builder` stage into this final clean image.
    *   *Order absolutely matters!* We copy them strictly in order of "least likely to change" to "most likely to change". 
    *   When you push a code update, Docker checks the layers. It sees the `dependencies` haven't changed and reuses the cached layer. It eventually reaches `application`, realizes the code changed, and only rebuilds that specific layer (a few Kilobytes). This makes pushing to GHCR and the Server pulling the image blazing fast.

*   `RUN chown -R appuser:appgroup /app`
    *   Because we created a restricted user, it cannot access files created by root. This command grants `appuser` ownership of the `/app` directory so it has permission to read and execute the Java files.

*   `USER appuser`
    *   Instructs Docker to physically switch the executing user context from `root` to `appuser`. The application is now running securely under the Principle of Least Privilege. If the app is compromised, the attacker is trapped as a powerless user.

*   `EXPOSE 8080`
    *   Documentation purpose only. Tells anyone reading this file that the container listens on port 8080 internally.

*   `ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]`
    *   The command that actually starts the Spring Boot application.
    *   Notice we are NOT running the traditional `java -jar app.jar`. Instead, we use Spring's highly optimized `JarLauncher`. This launcher knows how to stitch the 4 extracted component layers back together directly in RAM and boots the application up to 30% faster than standard Jar execution.
