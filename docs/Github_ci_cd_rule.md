# Travery Backend: Team Development Workflow Guide

## How We Work Together

---

## Table of Contents

1. [Overview](#overview)
2. [Branch Strategy](#branch-strategy)
3. [Daily Workflow](#daily-workflow)
4. [Commit & Push](#commit--push)
5. [Pull Request Process](#pull-request-process)
6. [Code Review Standards](#code-review-standards)
7. [CI/CD Pipeline](#cicd-pipeline)
8. [Deployment Rules](#deployment-rules)
9. [Hotfixes & Emergency](#hotfixes--emergency)
10. [Release & Versioning](#release--versioning)

---

# Overview

Welcome to the Travery Backend team! 👋 Here's how we work together to build and ship features safely and efficiently.

## Our Process in a Nutshell

```
You code a feature
    ↓
Create PR to test branch (team validation)
    ↓
Team reviews and integrates on test
    ↓
Create PR to main branch (ready for production)
    ↓
Final review and merge
    ↓
Automatic deployment to production! 🚀
```

---

# Branch Strategy

We use **3 main branches** in this repository:

## 1. Feature Branches (You work here)

**Naming:** `feature/<description>`, `bugfix/<description>`, `chore/<description>`

✅ Examples:

- `feature/user-authentication`
- `bugfix/payment-null-pointer`
- `chore/update-dependencies`

**What you do:**

- [ ] Create from latest `test` branch
- [ ] Work on your feature
- [ ] Keep it short (3-5 days max)
- [ ] Make small commits (more on this below)

**After you're done:**

- [ ] Push to GitHub
- [ ] Create PR → test branch
- [ ] Wait for team review

---

## 2. test Branch (Team validation zone)

**Purpose:** Integration point where multiple features come together

**What happens here:**

- All approved features are merged together
- Team tests everything works well together
- No deployment to any server happens here
- This is where we validate before production

**Your role:**

- [ ] Review what team members merged
- [ ] Test locally: `git checkout test && git pull origin test`
- [ ] Report if anything breaks
- [ ] Help fix integration issues

**Never:**

- ❌ Work directly on test branch
- ❌ Make random commits to test

---

## 3. main Branch (Production)

**Purpose:** Production-ready code that's deployed automatically

**What happens here:**

- Only code that was tested on test branch comes here
- Automatically deployed to Server 1 (production)
- Always stable and reliable

**Your role:**

- [ ] Don't work directly on main
- [ ] Review PRs from test → main (if you're lead dev)
- [ ] Monitor after deployment

**Never:**

- ❌ Direct commits to main
- ❌ Push code that wasn't tested on test

---

## GitHub Will Enforce

These rules are set in GitHub automatically. If you try to break them, GitHub will stop you:

- ✅ You can't commit directly to test or main (must use PR)
- ✅ You can't merge PR without approval
- ✅ CI/CD checks must pass before merge
- ✅ Your PR will show a status check (✓ passed or ✗ failed)

---

# Daily Workflow

## How Your Day Looks

### Morning: Start Your Feature

```bash
# Get the latest code
git checkout test
git pull origin test

# Create your feature branch
git checkout -b feature/your-feature-name

# Code for a few hours...
# Test locally
# Make commits (see next section)
```

### Afternoon: Push & Create PR

```bash
# Push your code
git push origin feature/your-feature-name

# Go to GitHub → Create Pull Request
# - Target: test branch
# - Add description (AI Agent can help with this)
# - Submit for review
```

### Next Day: Address Feedback

```bash
# Get feedback from team members
# Make changes
git add .
git commit -m "refactor: address code review feedback"
git push origin feature/your-feature-name

# GitHub automatically updates the PR
# Team reviews again
# When approved, merge!
```

---

# Commit & Push

## 📝 How to Write Commits

We follow **Conventional Commits** format. It's a standard way to write commit messages.

### 🤖 AI Agent Rule: Commit Messages

You can use AI Agent to help write commit messages! Here's how:

**Tell AI Agent:**

```
Generate a commit message for these changes:
[paste your code changes or describe what you did]

Use format: <type>(<scope>): <description>

Types: feat, fix, docs, style, refactor, perf, test, chore
Example: feat(auth): add JWT token refresh
```

**Then copy the generated message:**

```bash
git commit -m "feat(auth): add JWT token refresh"
```

### Types of Commits

| Type       | When to Use           | Example                               |
| ---------- | --------------------- | ------------------------------------- |
| `feat`     | New feature           | `feat(auth): add email verification`  |
| `fix`      | Bug fix               | `fix(booking): fix payment timeout`   |
| `refactor` | Code improvement      | `refactor(user): simplify validation` |
| `test`     | Add/update tests      | `test(auth): add login tests`         |
| `chore`    | Dependencies, tooling | `chore(deps): update Spring Boot`     |
| `docs`     | Documentation         | `docs: update README`                 |

### Good Commit Messages ✅

```
feat(auth): add JWT refresh token mechanism

Implement refresh token endpoint that allows users to extend
their session without re-authenticating. Tokens expire after
7 days or on logout.

Fixes: #123
```

### Bad Commit Messages ❌

```
fix bug
Update code
WIP
changes
```

---

## 🔧 Developer Rule: Make Small Commits

**Do:**

- [ ] Make **multiple small commits** (5-10 commits per feature is good)
- [ ] Each commit should be **one logical change**
- [ ] Commit frequently (not all at the end)

**Example of good commits:**

```
1. feat(auth): add user registration endpoint
2. feat(auth): implement email verification logic
3. test(auth): add registration tests
4. docs(auth): update API docs for registration
```

**Example of bad commits:**

```
1. feat: implemented entire auth system with email, jwt, refresh tokens, tests, and docs
```

**Why?** Small commits make it easier to understand what changed, review code, and fix issues later.

---

# Pull Request Process

## 🚀 Your PR Journey

### Step 1: Create PR

When your feature is ready:

```bash
git push origin feature/your-feature-name
```

Go to GitHub → Click "Create Pull Request"

### Step 2: Fill PR Description

**🤖 AI Agent Rule: PR Description**

AI Agent can help write your PR description! Tell it:

```
Generate a PR description for:
- What does this PR do?
- Why is it needed?
- What files changed?
- How to test?

Include:
- Description of changes
- Type (feat/fix/refactor)
- How to test
- Related issues (if any)
```

### Step 3: Submit & Wait for Review

Your PR now shows:

- 🔄 **CI checks running** (format check, tests)
- ⏳ **Waiting for review** from team

### Step 4: Address Feedback

When team members comment:

```bash
# Make the requested changes
# Add a new commit (don't force push!)
git add .
git commit -m "refactor: address code review feedback"
git push origin feature/your-feature-name
```

GitHub automatically updates your PR. Team reviews again.

### Step 5: Merge!

When everything looks good:

- ✅ CI passes (all checks green)
- ✅ At least 1 approval from team
- ✅ No conflicts

Click the **Merge** button! 🎉

**GitHub will auto-delete your feature branch.** Don't worry about cleanup.

---

## Rules GitHub Will Enforce

These happen automatically - you just need to know about them:

| Requirement           | What It Means                                                            |
| --------------------- | ------------------------------------------------------------------------ |
| **CI Must Pass**      | Your code must pass format check and tests. If red ✗, fix it             |
| **Approval Required** | At least 1 team member must approve. If no one approved, you can't merge |
| **No Direct Pushes**  | You can't commit directly to test/main. Must use PR                      |

---

# Code Review Standards

## 🧑‍💼 Developer Rule: When You Review Others' Code

When a team member creates a PR, you'll be asked to review it. Here's what to check:

### What to Look For

- [ ] **Does it work?** Code logic makes sense
- [ ] **Tests?** Are there tests for the new code?
- [ ] **Readable?** Would you understand it next week?
- [ ] **Safe?** No security issues, no hardcoded passwords
- [ ] **Follow standards?** Uses our coding style
- [ ] **Tested locally?** Ask if they tested it locally

### How to Give Feedback

**Good feedback:** 👍

```
"This logic could be simpler. Consider using Optional instead of null checks."
"Have you tested the edge case where the user has no bookings?"
"Nice! Follows the same pattern as UserService."
```

**Bad feedback:** 👎

```
"Bad code"
"Change this"
"I don't like it"
```

### When to Approve

Click **Approve** when:

- [ ] Code looks good
- [ ] Tests pass
- [ ] You've tested locally (if complex)
- [ ] All your feedback was addressed

**NEVER approve if:**

- ❌ Tests fail
- ❌ Code quality is poor
- ❌ You didn't understand it
- ❌ Your feedback wasn't addressed

---

# CI/CD Pipeline

## 🤖 What Happens Automatically

When you create a PR, GitHub automatically runs checks on your code. You don't need to do anything - it just happens!

### If You Create PR → test Branch

**What runs:**

1. ✅ **Format Check** - Makes sure your code is formatted correctly
2. ✅ **Build & Tests** - Compiles code and runs all tests
3. ❌ **NO Docker Build** (saves time, you're just testing)

**Status:** Shows on your PR as green ✅ (pass) or red ✗ (fail)

**If it fails:**

- Check the error message
- Fix the issue
- Push new commits
- It runs again automatically

### If You Create PR → main Branch

**What runs:**

1. ✅ **Format Check**
2. ✅ **Build & Tests**
3. ✅ **Docker Build** - Creates production image
4. ✅ **Push to Docker Registry** - Stores for deployment

This only happens when code comes from test branch and is ready for production.

### If You Merge to main

**What runs automatically:**

1. 🚀 **CD Pipeline** - Deploys to production Server
2. 🔄 **Pulls latest Docker image**
3. 🔄 **Restarts the app**
4. ✅ **Verifies it's running**

**No manual action needed!** It just happens.

---

## What You See on GitHub

Your PR will show a status like:

```
✅ All checks passed
├─ format-check ✓
├─ build-and-test ✓
└─ build-and-push ✓ (only for main PRs)
```

or

```
❌ Some checks failed
├─ format-check ✗ (failed)
└─ build-and-test (waiting...)
```

**Green = good, Red = fix it**

---

# Daily PR Example

## Real-World Scenario

```
Monday 10am:
  You: Create feature/user-dashboard branch
       Work on code
       Test locally: ./mvnw test ✓

Monday 3pm:
  You: git push origin feature/user-dashboard
       Create PR → test
  GitHub: Runs CI checks (format + test)
  GitHub: ✅ All checks passed!

Tuesday 10am:
  Teammate: Reviews code, leaves feedback
  You: Fix feedback, push new commits
  Teammate: ✅ Approves
  You: Click Merge!
  GitHub: Merges to test, deletes feature branch

Wednesday 10am:
  You: Help team test on test branch
       Everything works! ✓

Wednesday 2pm:
  You: Create PR: test → main
  GitHub: Runs CI checks (format + test + Docker build)
  GitHub: ✅ Docker image built and pushed!
  Lead Dev: ✅ Approves
  You: Click Merge!
  GitHub: Merges to main
  GitHub: Automatically triggers deployment!

Wednesday 3pm:
  ✅ Your feature is LIVE in production! 🎉
```

---

# Deployment Rules

## When Code Gets Deployed

✅ **Code gets deployed automatically when:**

- [ ] Your PR is merged to main
- [ ] CI checks all passed (green ✅)
- [ ] All team member approved the PR

❌ **Code will NOT be deployed if:**

- ✗ CI checks failed
- ✗ No approval yet
- ✗ Merged to test (test is for validation only)

---

## What Gets Deployed

When your code merges to main:

1. Docker image is built (contains your code)
2. Image is pushed to registry
3. Automatically deployed to **Server 1 (Production)**
4. App container restarts
5. Everything is verified to be running

**Total deployment time:** ~5 minutes

---

## After Deployment

**Your responsibility:**

- [ ] Check if app is working in production
- [ ] Monitor for 1 hour after deployment
- [ ] Report any issues immediately in Slack

**How to verify:**

```bash
# Check app is up
curl http://<server>/actuator/health

# Or check Grafana dashboard
# Look for metrics, logs, no error spikes
```

---

# Hotfixes & Emergency

## 🚨 Critical Bug in Production?

If something breaks in production, we need to fix it FAST.

### Emergency Deployment

**When:** Only for critical issues (app down, security, data loss)

**What you do:**

```bash
# Create hotfix branch from main
git checkout main
git pull origin main
git checkout -b hotfix/critical-issue-description

# Make MINIMAL changes to fix the issue
# Don't refactor, don't improve, just FIX

# Commit
git commit -m "fix(critical): describe the critical issue fix"
git push origin hotfix/critical-issue-description

# Create PR → main with [HOTFIX] prefix
# Title: [HOTFIX] Critical issue description
```

**What happens:**

1. CI runs quickly
2. Skip normal review (lead dev approves immediately)
3. Merge to main
4. Automatic deployment (5 minutes)
5. Issue is fixed! ✅

**After emergency is over:**

- [ ] Merge hotfix to test branch too
- [ ] Document what went wrong
- [ ] Discuss how to prevent next time

---

# Release & Versioning

## How We Version

We use **Semantic Versioning**: `MAJOR.MINOR.PATCH`

Examples:

- `1.0.0` - First release
- `1.1.0` - New features added
- `1.1.1` - Bug fix
- `2.0.0` - Breaking changes

---

## Release Process

### 1. Development Phase (Weekly)

- [ ] Features developed on feature branches
- [ ] Merged to test for team testing
- [ ] Team validates everything works together

### 2. Release Phase (When Ready)

- [ ] Create PR: test → main
- [ ] All checks pass
- [ ] Lead dev reviews
- [ ] Merge to main
- [ ] Automatic deployment to production

### 3. After Release

- [ ] Monitor metrics and logs
- [ ] Check no errors
- [ ] Announce in Slack

---

## Release Checklist

Before creating release PR (test → main):

- [ ] ✅ All features on test branch tested
- [ ] ✅ No breaking changes (or documented)
- [ ] ✅ README/docs updated
- [ ] ✅ Team agrees it's ready

**After deployment:**

- [ ] ✅ App running without errors
- [ ] ✅ No performance issues
- [ ] ✅ Users can use all features

---

# Summary: What You Need to Know

## 🟢 Your Responsibilities (Developer)

- [ ] Follow branch naming convention
- [ ] Make small, logical commits
- [ ] Test locally before pushing
- [ ] Create descriptive PRs
- [ ] Review other team members' PRs
- [ ] Address code review feedback
- [ ] Monitor deployments
- [ ] Report issues immediately
- [ ] Help with hotfixes if needed

---

## 🤖 AI Agent Can Help With

- [ ] Commit messages (using Conventional Commits format)
- [ ] PR descriptions (what changed, why, how to test)
- [ ] Code formatting (Prettier, ESLint, etc.)
- [ ] Boilerplate code generation
- [ ] Test skeleton generation

---

## 🔐 GitHub Enforces Automatically

- [ ] No direct commits to test/main (must use PR)
- [ ] CI checks must pass
- [ ] Need approval to merge
- [ ] Status checks visible on PR

---

## ⚡ What Happens Automatically

- [ ] **CI Pipeline:** Runs tests, format checks, builds Docker image
- [ ] **CD Pipeline:** Deploys to production when code merges to main
- [ ] **Branch deletion:** Feature branches auto-deleted after merge
- [ ] **PR status:** Shows pass/fail on your PR

---

## 🚀 Quick Start for New Team Members

1. **Read this guide** (you're doing it! ✓)
2. **Clone repository:** `git clone <repo>`
3. **Test locally:** `./mvnw clean verify`
4. **Create feature branch:** `git checkout -b feature/my-first-task`
5. **Make changes & commit:** Follow commit standards
6. **Push & create PR:** `git push origin feature/my-first-task`
7. **Get reviewed:** Wait for team feedback
8. **Address feedback:** Make new commits
9. **Merge:** Click merge when approved + CI passes
10. **Done!** Your code goes to test, then production 🎉

---

**Welcome to the team!** Let's build something great together! 🚀

# Travery Backend: Team Development Workflow Guide

## How We Work Together

---

## Table of Contents

1. [Overview](#overview)
2. [Branch Strategy](#branch-strategy)
3. [Daily Workflow](#daily-workflow)
4. [Commit & Push](#commit--push)
5. [Pull Request Process](#pull-request-process)
6. [Code Review Standards](#code-review-standards)
7. [CI/CD Pipeline](#cicd-pipeline)
8. [Deployment Rules](#deployment-rules)
9. [Hotfixes & Emergency](#hotfixes--emergency)
10. [Release & Versioning](#release--versioning)

---

# Overview

Welcome to the Travery Backend team! 👋 Here's how we work together to build and ship features safely and efficiently.

## Our Process in a Nutshell

```
You code a feature
    ↓
Create PR to test branch (team validation)
    ↓
Team reviews and integrates on test
    ↓
Create PR to main branch (ready for production)
    ↓
Final review and merge
    ↓
Automatic deployment to production! 🚀
```

---

# Branch Strategy

We use **3 main branches** in this repository:

## 1. Feature Branches (You work here)

**Naming:** `feature/<description>`, `bugfix/<description>`, `chore/<description>`

✅ Examples:

- `feature/user-authentication`
- `bugfix/payment-null-pointer`
- `chore/update-dependencies`

**What you do:**

- [ ] Create from latest `test` branch
- [ ] Work on your feature
- [ ] Keep it short (3-5 days max)
- [ ] Make small commits (more on this below)

**After you're done:**

- [ ] Push to GitHub
- [ ] Create PR → test branch
- [ ] Wait for team review

---

## 2. test Branch (Team validation zone)

**Purpose:** Integration point where multiple features come together

**What happens here:**

- All approved features are merged together
- Team tests everything works well together
- No deployment to any server happens here
- This is where we validate before production

**Your role:**

- [ ] Review what team members merged
- [ ] Test locally: `git checkout test && git pull origin test`
- [ ] Report if anything breaks
- [ ] Help fix integration issues

**Never:**

- ❌ Work directly on test branch
- ❌ Make random commits to test

---

## 3. main Branch (Production)

**Purpose:** Production-ready code that's deployed automatically

**What happens here:**

- Only code that was tested on test branch comes here
- Automatically deployed to Server 1 (production)
- Always stable and reliable

**Your role:**

- [ ] Don't work directly on main
- [ ] Review PRs from test → main (if you're lead dev)
- [ ] Monitor after deployment

**Never:**

- ❌ Direct commits to main
- ❌ Push code that wasn't tested on test

---

## GitHub Will Enforce

These rules are set in GitHub automatically. If you try to break them, GitHub will stop you:

- ✅ You can't commit directly to test or main (must use PR)
- ✅ You can't merge PR without approval
- ✅ CI/CD checks must pass before merge
- ✅ Your PR will show a status check (✓ passed or ✗ failed)

---

# Daily Workflow

## How Your Day Looks

### Morning: Start Your Feature

```bash
# Get the latest code
git checkout test
git pull origin test

# Create your feature branch
git checkout -b feature/your-feature-name

# Code for a few hours...
# Test locally
# Make commits (see next section)
```

### Afternoon: Push & Create PR

```bash
# Push your code
git push origin feature/your-feature-name

# Go to GitHub → Create Pull Request
# - Target: test branch
# - Add description (AI Agent can help with this)
# - Submit for review
```

### Next Day: Address Feedback

```bash
# Get feedback from team members
# Make changes
git add .
git commit -m "refactor: address code review feedback"
git push origin feature/your-feature-name

# GitHub automatically updates the PR
# Team reviews again
# When approved, merge!
```

---

# Commit & Push

## 📝 How to Write Commits

We follow **Conventional Commits** format. It's a standard way to write commit messages.

### 🤖 AI Agent Rule: Commit Messages

You can use AI Agent to help write commit messages! Here's how:

**Tell AI Agent:**

```
Generate a commit message for these changes:
[paste your code changes or describe what you did]

Use format: <type>(<scope>): <description>

Types: feat, fix, docs, style, refactor, perf, test, chore
Example: feat(auth): add JWT token refresh
```

**Then copy the generated message:**

```bash
git commit -m "feat(auth): add JWT token refresh"
```

### Types of Commits

| Type       | When to Use           | Example                               |
| ---------- | --------------------- | ------------------------------------- |
| `feat`     | New feature           | `feat(auth): add email verification`  |
| `fix`      | Bug fix               | `fix(booking): fix payment timeout`   |
| `refactor` | Code improvement      | `refactor(user): simplify validation` |
| `test`     | Add/update tests      | `test(auth): add login tests`         |
| `chore`    | Dependencies, tooling | `chore(deps): update Spring Boot`     |
| `docs`     | Documentation         | `docs: update README`                 |

### Good Commit Messages ✅

```
feat(auth): add JWT refresh token mechanism

Implement refresh token endpoint that allows users to extend
their session without re-authenticating. Tokens expire after
7 days or on logout.

Fixes: #123
```

### Bad Commit Messages ❌

```
fix bug
Update code
WIP
changes
```

---

## 🔧 Developer Rule: Make Small Commits

**Do:**

- [ ] Make **multiple small commits** (5-10 commits per feature is good)
- [ ] Each commit should be **one logical change**
- [ ] Commit frequently (not all at the end)

**Example of good commits:**

```
1. feat(auth): add user registration endpoint
2. feat(auth): implement email verification logic
3. test(auth): add registration tests
4. docs(auth): update API docs for registration
```

**Example of bad commits:**

```
1. feat: implemented entire auth system with email, jwt, refresh tokens, tests, and docs
```

**Why?** Small commits make it easier to understand what changed, review code, and fix issues later.

---

# Pull Request Process

## 🚀 Your PR Journey

### Step 1: Create PR

When your feature is ready:

```bash
git push origin feature/your-feature-name
```

Go to GitHub → Click "Create Pull Request"

### Step 2: Fill PR Description

**🤖 AI Agent Rule: PR Description**

AI Agent can help write your PR description! Tell it:

```
Generate a PR description for:
- What does this PR do?
- Why is it needed?
- What files changed?
- How to test?

Include:
- Description of changes
- Type (feat/fix/refactor)
- How to test
- Related issues (if any)
```

### Step 3: Submit & Wait for Review

Your PR now shows:

- 🔄 **CI checks running** (format check, tests)
- ⏳ **Waiting for review** from team

### Step 4: Address Feedback

When team members comment:

```bash
# Make the requested changes
# Add a new commit (don't force push!)
git add .
git commit -m "refactor: address code review feedback"
git push origin feature/your-feature-name
```

GitHub automatically updates your PR. Team reviews again.

### Step 5: Merge!

When everything looks good:

- ✅ CI passes (all checks green)
- ✅ At least 1 approval from team
- ✅ No conflicts

Click the **Merge** button! 🎉

**GitHub will auto-delete your feature branch.** Don't worry about cleanup.

---

## Rules GitHub Will Enforce

These happen automatically - you just need to know about them:

| Requirement           | What It Means                                                            |
| --------------------- | ------------------------------------------------------------------------ |
| **CI Must Pass**      | Your code must pass format check and tests. If red ✗, fix it             |
| **Approval Required** | At least 1 team member must approve. If no one approved, you can't merge |
| **No Direct Pushes**  | You can't commit directly to test/main. Must use PR                      |

---

# Code Review Standards

## 🧑‍💼 Developer Rule: When You Review Others' Code

When a team member creates a PR, you'll be asked to review it. Here's what to check:

### What to Look For

- [ ] **Does it work?** Code logic makes sense
- [ ] **Tests?** Are there tests for the new code?
- [ ] **Readable?** Would you understand it next week?
- [ ] **Safe?** No security issues, no hardcoded passwords
- [ ] **Follow standards?** Uses our coding style
- [ ] **Tested locally?** Ask if they tested it locally

### How to Give Feedback

**Good feedback:** 👍

```
"This logic could be simpler. Consider using Optional instead of null checks."
"Have you tested the edge case where the user has no bookings?"
"Nice! Follows the same pattern as UserService."
```

**Bad feedback:** 👎

```
"Bad code"
"Change this"
"I don't like it"
```

### When to Approve

Click **Approve** when:

- [ ] Code looks good
- [ ] Tests pass
- [ ] You've tested locally (if complex)
- [ ] All your feedback was addressed

**NEVER approve if:**

- ❌ Tests fail
- ❌ Code quality is poor
- ❌ You didn't understand it
- ❌ Your feedback wasn't addressed

---

# CI/CD Pipeline

## 🤖 What Happens Automatically

When you create a PR, GitHub automatically runs checks on your code. You don't need to do anything - it just happens!

### If You Create PR → test Branch

**What runs:**

1. ✅ **Format Check** - Makes sure your code is formatted correctly
2. ✅ **Build & Tests** - Compiles code and runs all tests
3. ❌ **NO Docker Build** (saves time, you're just testing)

**Status:** Shows on your PR as green ✅ (pass) or red ✗ (fail)

**If it fails:**

- Check the error message
- Fix the issue
- Push new commits
- It runs again automatically

### If You Create PR → main Branch

**What runs:**

1. ✅ **Format Check**
2. ✅ **Build & Tests**
3. ✅ **Docker Build** - Creates production image
4. ✅ **Push to Docker Registry** - Stores for deployment

This only happens when code comes from test branch and is ready for production.

### If You Merge to main

**What runs automatically:**

1. 🚀 **CD Pipeline** - Deploys to production Server
2. 🔄 **Pulls latest Docker image**
3. 🔄 **Restarts the app**
4. ✅ **Verifies it's running**

**No manual action needed!** It just happens.

---

## What You See on GitHub

Your PR will show a status like:

```
✅ All checks passed
├─ format-check ✓
├─ build-and-test ✓
└─ build-and-push ✓ (only for main PRs)
```

or

```
❌ Some checks failed
├─ format-check ✗ (failed)
└─ build-and-test (waiting...)
```

**Green = good, Red = fix it**

---

# Daily PR Example

## Real-World Scenario

```
Monday 10am:
  You: Create feature/user-dashboard branch
       Work on code
       Test locally: ./mvnw test ✓

Monday 3pm:
  You: git push origin feature/user-dashboard
       Create PR → test
  GitHub: Runs CI checks (format + test)
  GitHub: ✅ All checks passed!

Tuesday 10am:
  Teammate: Reviews code, leaves feedback
  You: Fix feedback, push new commits
  Teammate: ✅ Approves
  You: Click Merge!
  GitHub: Merges to test, deletes feature branch

Wednesday 10am:
  You: Help team test on test branch
       Everything works! ✓

Wednesday 2pm:
  You: Create PR: test → main
  GitHub: Runs CI checks (format + test + Docker build)
  GitHub: ✅ Docker image built and pushed!
  Lead Dev: ✅ Approves
  You: Click Merge!
  GitHub: Merges to main
  GitHub: Automatically triggers deployment!

Wednesday 3pm:
  ✅ Your feature is LIVE in production! 🎉
```

---

# Deployment Rules

## When Code Gets Deployed

✅ **Code gets deployed automatically when:**

- [ ] Your PR is merged to main
- [ ] CI checks all passed (green ✅)
- [ ] All team member approved the PR

❌ **Code will NOT be deployed if:**

- ✗ CI checks failed
- ✗ No approval yet
- ✗ Merged to test (test is for validation only)

---

## What Gets Deployed

When your code merges to main:

1. Docker image is built (contains your code)
2. Image is pushed to registry
3. Automatically deployed to **Server 1 (Production)**
4. App container restarts
5. Everything is verified to be running

**Total deployment time:** ~5 minutes

---

## After Deployment

**Your responsibility:**

- [ ] Check if app is working in production
- [ ] Monitor for 1 hour after deployment
- [ ] Report any issues immediately in Slack

**How to verify:**

```bash
# Check app is up
curl http://<server>/actuator/health

# Or check Grafana dashboard
# Look for metrics, logs, no error spikes
```

---

# Hotfixes & Emergency

## 🚨 Critical Bug in Production?

If something breaks in production, we need to fix it FAST.

### Emergency Deployment

**When:** Only for critical issues (app down, security, data loss)

**What you do:**

```bash
# Create hotfix branch from main
git checkout main
git pull origin main
git checkout -b hotfix/critical-issue-description

# Make MINIMAL changes to fix the issue
# Don't refactor, don't improve, just FIX

# Commit
git commit -m "fix(critical): describe the critical issue fix"
git push origin hotfix/critical-issue-description

# Create PR → main with [HOTFIX] prefix
# Title: [HOTFIX] Critical issue description
```

**What happens:**

1. CI runs quickly
2. Skip normal review (lead dev approves immediately)
3. Merge to main
4. Automatic deployment (5 minutes)
5. Issue is fixed! ✅

**After emergency is over:**

- [ ] Merge hotfix to test branch too
- [ ] Document what went wrong
- [ ] Discuss how to prevent next time

---

# Release & Versioning

## How We Version

We use **Semantic Versioning**: `MAJOR.MINOR.PATCH`

Examples:

- `1.0.0` - First release
- `1.1.0` - New features added
- `1.1.1` - Bug fix
- `2.0.0` - Breaking changes

---

## Release Process

### 1. Development Phase (Weekly)

- [ ] Features developed on feature branches
- [ ] Merged to test for team testing
- [ ] Team validates everything works together

### 2. Release Phase (When Ready)

- [ ] Create PR: test → main
- [ ] All checks pass
- [ ] Lead dev reviews
- [ ] Merge to main
- [ ] Automatic deployment to production

### 3. After Release

- [ ] Monitor metrics and logs
- [ ] Check no errors
- [ ] Announce in Slack

---

## Release Checklist

Before creating release PR (test → main):

- [ ] ✅ All features on test branch tested
- [ ] ✅ No breaking changes (or documented)
- [ ] ✅ README/docs updated
- [ ] ✅ Team agrees it's ready

**After deployment:**

- [ ] ✅ App running without errors
- [ ] ✅ No performance issues
- [ ] ✅ Users can use all features

---

# Summary: What You Need to Know

## 🟢 Your Responsibilities (Developer)

- [ ] Follow branch naming convention
- [ ] Make small, logical commits
- [ ] Test locally before pushing
- [ ] Create descriptive PRs
- [ ] Review other team members' PRs
- [ ] Address code review feedback
- [ ] Monitor deployments
- [ ] Report issues immediately
- [ ] Help with hotfixes if needed

---

## 🤖 AI Agent Can Help With

- [ ] Commit messages (using Conventional Commits format)
- [ ] PR descriptions (what changed, why, how to test)
- [ ] Code formatting (Prettier, ESLint, etc.)
- [ ] Boilerplate code generation
- [ ] Test skeleton generation

---

## 🔐 GitHub Enforces Automatically

- [ ] No direct commits to test/main (must use PR)
- [ ] CI checks must pass
- [ ] Need approval to merge
- [ ] Status checks visible on PR

---

## ⚡ What Happens Automatically

- [ ] **CI Pipeline:** Runs tests, format checks, builds Docker image
- [ ] **CD Pipeline:** Deploys to production when code merges to main
- [ ] **Branch deletion:** Feature branches auto-deleted after merge
- [ ] **PR status:** Shows pass/fail on your PR

---

## 🚀 Quick Start for New Team Members

1. **Read this guide** (you're doing it! ✓)
2. **Clone repository:** `git clone <repo>`
3. **Test locally:** `./mvnw clean verify`
4. **Create feature branch:** `git checkout -b feature/my-first-task`
5. **Make changes & commit:** Follow commit standards
6. **Push & create PR:** `git push origin feature/my-first-task`
7. **Get reviewed:** Wait for team feedback
8. **Address feedback:** Make new commits
9. **Merge:** Click merge when approved + CI passes
10. **Done!** Your code goes to test, then production 🎉

---

**Welcome to the team!** Let's build something great together! 🚀# Travery Backend: Team Development Workflow Guide

## How We Work Together

---

## Table of Contents

1. [Overview](#overview)
2. [Branch Strategy](#branch-strategy)
3. [Daily Workflow](#daily-workflow)
4. [Commit & Push](#commit--push)
5. [Pull Request Process](#pull-request-process)
6. [Code Review Standards](#code-review-standards)
7. [CI/CD Pipeline](#cicd-pipeline)
8. [Deployment Rules](#deployment-rules)
9. [Hotfixes & Emergency](#hotfixes--emergency)
10. [Release & Versioning](#release--versioning)

---

# Overview

Welcome to the Travery Backend team! 👋 Here's how we work together to build and ship features safely and efficiently.

## Our Process in a Nutshell

```
You code a feature
    ↓
Create PR to test branch (team validation)
    ↓
Team reviews and integrates on test
    ↓
Create PR to main branch (ready for production)
    ↓
Final review and merge
    ↓
Automatic deployment to production! 🚀
```

---

# Branch Strategy

We use **3 main branches** in this repository:

## 1. Feature Branches (You work here)

**Naming:** `feature/<description>`, `bugfix/<description>`, `chore/<description>`

✅ Examples:

- `feature/user-authentication`
- `bugfix/payment-null-pointer`
- `chore/update-dependencies`

**What you do:**

- [ ] Create from latest `test` branch
- [ ] Work on your feature
- [ ] Keep it short (3-5 days max)
- [ ] Make small commits (more on this below)

**After you're done:**

- [ ] Push to GitHub
- [ ] Create PR → test branch
- [ ] Wait for team review

---

## 2. test Branch (Team validation zone)

**Purpose:** Integration point where multiple features come together

**What happens here:**

- All approved features are merged together
- Team tests everything works well together
- No deployment to any server happens here
- This is where we validate before production

**Your role:**

- [ ] Review what team members merged
- [ ] Test locally: `git checkout test && git pull origin test`
- [ ] Report if anything breaks
- [ ] Help fix integration issues

**Never:**

- ❌ Work directly on test branch
- ❌ Make random commits to test

---

## 3. main Branch (Production)

**Purpose:** Production-ready code that's deployed automatically

**What happens here:**

- Only code that was tested on test branch comes here
- Automatically deployed to Server 1 (production)
- Always stable and reliable

**Your role:**

- [ ] Don't work directly on main
- [ ] Review PRs from test → main (if you're lead dev)
- [ ] Monitor after deployment

**Never:**

- ❌ Direct commits to main
- ❌ Push code that wasn't tested on test

---

## GitHub Will Enforce

These rules are set in GitHub automatically. If you try to break them, GitHub will stop you:

- ✅ You can't commit directly to test or main (must use PR)
- ✅ You can't merge PR without approval
- ✅ CI/CD checks must pass before merge
- ✅ Your PR will show a status check (✓ passed or ✗ failed)

---

# Daily Workflow

## How Your Day Looks

### Morning: Start Your Feature

```bash
# Get the latest code
git checkout test
git pull origin test

# Create your feature branch
git checkout -b feature/your-feature-name

# Code for a few hours...
# Test locally
# Make commits (see next section)
```

### Afternoon: Push & Create PR

```bash
# Push your code
git push origin feature/your-feature-name

# Go to GitHub → Create Pull Request
# - Target: test branch
# - Add description (AI Agent can help with this)
# - Submit for review
```

### Next Day: Address Feedback

```bash
# Get feedback from team members
# Make changes
git add .
git commit -m "refactor: address code review feedback"
git push origin feature/your-feature-name

# GitHub automatically updates the PR
# Team reviews again
# When approved, merge!
```

---

# Commit & Push

## 📝 How to Write Commits

We follow **Conventional Commits** format. It's a standard way to write commit messages.

### 🤖 AI Agent Rule: Commit Messages

You can use AI Agent to help write commit messages! Here's how:

**Tell AI Agent:**

```
Generate a commit message for these changes:
[paste your code changes or describe what you did]

Use format: <type>(<scope>): <description>

Types: feat, fix, docs, style, refactor, perf, test, chore
Example: feat(auth): add JWT token refresh
```

**Then copy the generated message:**

```bash
git commit -m "feat(auth): add JWT token refresh"
```

### Types of Commits

| Type       | When to Use           | Example                               |
| ---------- | --------------------- | ------------------------------------- |
| `feat`     | New feature           | `feat(auth): add email verification`  |
| `fix`      | Bug fix               | `fix(booking): fix payment timeout`   |
| `refactor` | Code improvement      | `refactor(user): simplify validation` |
| `test`     | Add/update tests      | `test(auth): add login tests`         |
| `chore`    | Dependencies, tooling | `chore(deps): update Spring Boot`     |
| `docs`     | Documentation         | `docs: update README`                 |

### Good Commit Messages ✅

```
feat(auth): add JWT refresh token mechanism

Implement refresh token endpoint that allows users to extend
their session without re-authenticating. Tokens expire after
7 days or on logout.

Fixes: #123
```

### Bad Commit Messages ❌

```
fix bug
Update code
WIP
changes
```

---

## 🔧 Developer Rule: Make Small Commits

**Do:**

- [ ] Make **multiple small commits** (5-10 commits per feature is good)
- [ ] Each commit should be **one logical change**
- [ ] Commit frequently (not all at the end)

**Example of good commits:**

```
1. feat(auth): add user registration endpoint
2. feat(auth): implement email verification logic
3. test(auth): add registration tests
4. docs(auth): update API docs for registration
```

**Example of bad commits:**

```
1. feat: implemented entire auth system with email, jwt, refresh tokens, tests, and docs
```

**Why?** Small commits make it easier to understand what changed, review code, and fix issues later.

---

# Pull Request Process

## 🚀 Your PR Journey

### Step 1: Create PR

When your feature is ready:

```bash
git push origin feature/your-feature-name
```

Go to GitHub → Click "Create Pull Request"

### Step 2: Fill PR Description

**🤖 AI Agent Rule: PR Description**

AI Agent can help write your PR description! Tell it:

```
Generate a PR description for:
- What does this PR do?
- Why is it needed?
- What files changed?
- How to test?

Include:
- Description of changes
- Type (feat/fix/refactor)
- How to test
- Related issues (if any)
```

### Step 3: Submit & Wait for Review

Your PR now shows:

- 🔄 **CI checks running** (format check, tests)
- ⏳ **Waiting for review** from team

### Step 4: Address Feedback

When team members comment:

```bash
# Make the requested changes
# Add a new commit (don't force push!)
git add .
git commit -m "refactor: address code review feedback"
git push origin feature/your-feature-name
```

GitHub automatically updates your PR. Team reviews again.

### Step 5: Merge!

When everything looks good:

- ✅ CI passes (all checks green)
- ✅ At least 1 approval from team
- ✅ No conflicts

Click the **Merge** button! 🎉

**GitHub will auto-delete your feature branch.** Don't worry about cleanup.

---

## Rules GitHub Will Enforce

These happen automatically - you just need to know about them:

| Requirement           | What It Means                                                            |
| --------------------- | ------------------------------------------------------------------------ |
| **CI Must Pass**      | Your code must pass format check and tests. If red ✗, fix it             |
| **Approval Required** | At least 1 team member must approve. If no one approved, you can't merge |
| **No Direct Pushes**  | You can't commit directly to test/main. Must use PR                      |

---

# Code Review Standards

## 🧑‍💼 Developer Rule: When You Review Others' Code

When a team member creates a PR, you'll be asked to review it. Here's what to check:

### What to Look For

- [ ] **Does it work?** Code logic makes sense
- [ ] **Tests?** Are there tests for the new code?
- [ ] **Readable?** Would you understand it next week?
- [ ] **Safe?** No security issues, no hardcoded passwords
- [ ] **Follow standards?** Uses our coding style
- [ ] **Tested locally?** Ask if they tested it locally

### How to Give Feedback

**Good feedback:** 👍

```
"This logic could be simpler. Consider using Optional instead of null checks."
"Have you tested the edge case where the user has no bookings?"
"Nice! Follows the same pattern as UserService."
```

**Bad feedback:** 👎

```
"Bad code"
"Change this"
"I don't like it"
```

### When to Approve

Click **Approve** when:

- [ ] Code looks good
- [ ] Tests pass
- [ ] You've tested locally (if complex)
- [ ] All your feedback was addressed

**NEVER approve if:**

- ❌ Tests fail
- ❌ Code quality is poor
- ❌ You didn't understand it
- ❌ Your feedback wasn't addressed

---

# CI/CD Pipeline

## 🤖 What Happens Automatically

When you create a PR, GitHub automatically runs checks on your code. You don't need to do anything - it just happens!

### If You Create PR → test Branch

**What runs:**

1. ✅ **Format Check** - Makes sure your code is formatted correctly
2. ✅ **Build & Tests** - Compiles code and runs all tests
3. ❌ **NO Docker Build** (saves time, you're just testing)

**Status:** Shows on your PR as green ✅ (pass) or red ✗ (fail)

**If it fails:**

- Check the error message
- Fix the issue
- Push new commits
- It runs again automatically

### If You Create PR → main Branch

**What runs:**

1. ✅ **Format Check**
2. ✅ **Build & Tests**
3. ✅ **Docker Build** - Creates production image
4. ✅ **Push to Docker Registry** - Stores for deployment

This only happens when code comes from test branch and is ready for production.

### If You Merge to main

**What runs automatically:**

1. 🚀 **CD Pipeline** - Deploys to production Server
2. 🔄 **Pulls latest Docker image**
3. 🔄 **Restarts the app**
4. ✅ **Verifies it's running**

**No manual action needed!** It just happens.

---

## What You See on GitHub

Your PR will show a status like:

```
✅ All checks passed
├─ format-check ✓
├─ build-and-test ✓
└─ build-and-push ✓ (only for main PRs)
```

or

```
❌ Some checks failed
├─ format-check ✗ (failed)
└─ build-and-test (waiting...)
```

**Green = good, Red = fix it**

---

# Daily PR Example

## Real-World Scenario

```
Monday 10am:
  You: Create feature/user-dashboard branch
       Work on code
       Test locally: ./mvnw test ✓

Monday 3pm:
  You: git push origin feature/user-dashboard
       Create PR → test
  GitHub: Runs CI checks (format + test)
  GitHub: ✅ All checks passed!

Tuesday 10am:
  Teammate: Reviews code, leaves feedback
  You: Fix feedback, push new commits
  Teammate: ✅ Approves
  You: Click Merge!
  GitHub: Merges to test, deletes feature branch

Wednesday 10am:
  You: Help team test on test branch
       Everything works! ✓

Wednesday 2pm:
  You: Create PR: test → main
  GitHub: Runs CI checks (format + test + Docker build)
  GitHub: ✅ Docker image built and pushed!
  Lead Dev: ✅ Approves
  You: Click Merge!
  GitHub: Merges to main
  GitHub: Automatically triggers deployment!

Wednesday 3pm:
  ✅ Your feature is LIVE in production! 🎉
```

---

# Deployment Rules

## When Code Gets Deployed

✅ **Code gets deployed automatically when:**

- [ ] Your PR is merged to main
- [ ] CI checks all passed (green ✅)
- [ ] All team member approved the PR

❌ **Code will NOT be deployed if:**

- ✗ CI checks failed
- ✗ No approval yet
- ✗ Merged to test (test is for validation only)

---

## What Gets Deployed

When your code merges to main:

1. Docker image is built (contains your code)
2. Image is pushed to registry
3. Automatically deployed to **Server 1 (Production)**
4. App container restarts
5. Everything is verified to be running

**Total deployment time:** ~5 minutes

---

## After Deployment

**Your responsibility:**

- [ ] Check if app is working in production
- [ ] Monitor for 1 hour after deployment
- [ ] Report any issues immediately in Slack

**How to verify:**

```bash
# Check app is up
curl http://<server>/actuator/health

# Or check Grafana dashboard
# Look for metrics, logs, no error spikes
```

---

# Hotfixes & Emergency

## 🚨 Critical Bug in Production?

If something breaks in production, we need to fix it FAST.

### Emergency Deployment

**When:** Only for critical issues (app down, security, data loss)

**What you do:**

```bash
# Create hotfix branch from main
git checkout main
git pull origin main
git checkout -b hotfix/critical-issue-description

# Make MINIMAL changes to fix the issue
# Don't refactor, don't improve, just FIX

# Commit
git commit -m "fix(critical): describe the critical issue fix"
git push origin hotfix/critical-issue-description

# Create PR → main with [HOTFIX] prefix
# Title: [HOTFIX] Critical issue description
```

**What happens:**

1. CI runs quickly
2. Skip normal review (lead dev approves immediately)
3. Merge to main
4. Automatic deployment (5 minutes)
5. Issue is fixed! ✅

**After emergency is over:**

- [ ] Merge hotfix to test branch too
- [ ] Document what went wrong
- [ ] Discuss how to prevent next time

---

# Release & Versioning

## How We Version

We use **Semantic Versioning**: `MAJOR.MINOR.PATCH`

Examples:

- `1.0.0` - First release
- `1.1.0` - New features added
- `1.1.1` - Bug fix
- `2.0.0` - Breaking changes

---

## Release Process

### 1. Development Phase (Weekly)

- [ ] Features developed on feature branches
- [ ] Merged to test for team testing
- [ ] Team validates everything works together

### 2. Release Phase (When Ready)

- [ ] Create PR: test → main
- [ ] All checks pass
- [ ] Lead dev reviews
- [ ] Merge to main
- [ ] Automatic deployment to production

### 3. After Release

- [ ] Monitor metrics and logs
- [ ] Check no errors
- [ ] Announce in Slack

---

## Release Checklist

Before creating release PR (test → main):

- [ ] ✅ All features on test branch tested
- [ ] ✅ No breaking changes (or documented)
- [ ] ✅ README/docs updated
- [ ] ✅ Team agrees it's ready

**After deployment:**

- [ ] ✅ App running without errors
- [ ] ✅ No performance issues
- [ ] ✅ Users can use all features

---

# Summary: What You Need to Know

## 🟢 Your Responsibilities (Developer)

- [ ] Follow branch naming convention
- [ ] Make small, logical commits
- [ ] Test locally before pushing
- [ ] Create descriptive PRs
- [ ] Review other team members' PRs
- [ ] Address code review feedback
- [ ] Monitor deployments
- [ ] Report issues immediately
- [ ] Help with hotfixes if needed

---

## 🤖 AI Agent Can Help With

- [ ] Commit messages (using Conventional Commits format)
- [ ] PR descriptions (what changed, why, how to test)
- [ ] Code formatting (Prettier, ESLint, etc.)
- [ ] Boilerplate code generation
- [ ] Test skeleton generation

---

## 🔐 GitHub Enforces Automatically

- [ ] No direct commits to test/main (must use PR)
- [ ] CI checks must pass
- [ ] Need approval to merge
- [ ] Status checks visible on PR

---

## ⚡ What Happens Automatically

- [ ] **CI Pipeline:** Runs tests, format checks, builds Docker image
- [ ] **CD Pipeline:** Deploys to production when code merges to main
- [ ] **Branch deletion:** Feature branches auto-deleted after merge
- [ ] **PR status:** Shows pass/fail on your PR

---

## 🚀 Quick Start for New Team Members

1. **Read this guide** (you're doing it! ✓)
2. **Clone repository:** `git clone <repo>`
3. **Test locally:** `./mvnw clean verify`
4. **Create feature branch:** `git checkout -b feature/my-first-task`
5. **Make changes & commit:** Follow commit standards
6. **Push & create PR:** `git push origin feature/my-first-task`
7. **Get reviewed:** Wait for team feedback
8. **Address feedback:** Make new commits
9. **Merge:** Click merge when approved + CI passes
10. **Done!** Your code goes to test, then production 🎉

---

**Welcome to the team!** Let's build something great together! 🚀
