
    create table add_on_orders (
        quantity integer not null,
        total_price numeric(12,2) not null,
        created_at timestamp(6) not null,
        scheduled_time timestamp(6) not null,
        updated_at timestamp(6) not null,
        hotel_booking_id uuid not null,
        hotel_service_id uuid not null,
        id uuid not null,
        status varchar(50) check ((status in ('PENDING','DELIVERED','CANCELLED'))),
        primary key (id)
    );

    create table admins (
        id uuid not null,
        primary key (id)
    );

    create table amenities (
        is_active boolean,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        type varchar(50) not null check ((type in ('HOTEL_AMENITY','ROOM_AMENITY'))),
        name varchar(100) not null unique,
        icon_public_id varchar(255),
        icon_url varchar(255),
        primary key (id)
    );

    create table booking_members (
        date_of_birth date,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        booking_id uuid not null,
        id uuid not null,
        member_type varchar(20) not null check ((member_type in ('ADULT','CHILD'))),
        attendance_status varchar(50) not null check ((attendance_status in ('NOT_CHECKED','PRESENT','NO_SHOW'))),
        booking_type varchar(50) not null check ((booking_type in ('TOUR_BOOKING','HOTEL_BOOKING','COACH_BOOKING'))),
        identity_number varchar(50) not null,
        full_name varchar(100) not null,
        primary key (id)
    );

    create table chat_sessions (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        coordinator_id uuid,
        id uuid not null,
        tour_id uuid,
        tour_instance_id uuid,
        user_id uuid not null,
        status varchar(20) check ((status in ('OPEN','CLOSED'))),
        cometchat_guid varchar(100) not null unique,
        primary key (id)
    );

    create table coach_booking_seats (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        coach_booking_id uuid not null,
        id uuid not null,
        seat_layout_item_id uuid not null,
        primary key (id),
        unique (coach_booking_id, seat_layout_item_id)
    );

    create table coach_bookings (
        base_price numeric(12,2) not null,
        total_price numeric(12,2) not null,
        created_at timestamp(6) not null,
        payment_deadline timestamp(6),
        updated_at timestamp(6) not null,
        coach_trip_id uuid not null,
        id uuid not null,
        user_id uuid not null,
        contact_phone varchar(20),
        status varchar(50) check ((status in ('PENDING','PAID','CHECKED_IN','CHECKED_OUT','CANCELLED','NO_SHOW'))),
        contact_name varchar(100),
        primary key (id)
    );

    create table coach_trips (
        arrival_time timestamp(6),
        created_at timestamp(6) not null,
        departure_time timestamp(6) not null,
        updated_at timestamp(6) not null,
        coach_id uuid not null,
        coordinator_id uuid not null,
        driver_id uuid not null,
        id uuid not null,
        route_id uuid not null,
        status varchar(50) check ((status in ('OPEN','FULL','IN_PROGRESS','COMPLETED','CANCELLED'))),
        primary key (id)
    );

    create table coaches (
        capacity integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        seat_layout_id uuid,
        license_plate varchar(20) not null unique,
        status varchar(20) check ((status in ('ACTIVE','MAINTENANCE'))),
        coach_type varchar(50) not null check ((coach_type in ('SEAT','BED','LIMOUSINE'))),
        primary key (id)
    );

    create table coordinators (
        id uuid not null,
        department varchar(50) check ((department in ('TOUR','HOTEL','COACH'))),
        employee_code varchar(50) not null unique,
        primary key (id)
    );

    create table destinations (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        code varchar(50) not null unique,
        name varchar(255) not null,
        primary key (id)
    );

    create table drivers (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        phone_number varchar(20) not null unique,
        status varchar(20) check ((status in ('AVAILABLE','ON_TRIP','ON_LEAVE'))),
        license_number varchar(50) not null unique,
        full_name varchar(100) not null,
        avatar_url varchar(500),
        avatar_public_id varchar(255),
        primary key (id)
    );

    create table guides (
        years_experience integer,
        id uuid not null,
        employee_code varchar(50) not null unique,
        guide_license varchar(100) not null unique,
        languages jsonb,
        primary key (id)
    );

    create table hotel_amenities (
        amenity_id uuid not null,
        hotel_id uuid not null,
        primary key (amenity_id, hotel_id)
    );

    create table hotel_booking_details (
        price_at_booking numeric(12,2) not null,
        quantity integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        hotel_booking_id uuid not null,
        id uuid not null,
        room_type_id uuid not null,
        primary key (id)
    );

    create table hotel_bookings (
        end_date date not null,
        start_date date not null,
        total_price numeric(12,2) not null,
        actual_check_in_time timestamp(6),
        actual_check_out_time timestamp(6),
        created_at timestamp(6) not null,
        payment_deadline timestamp(6),
        updated_at timestamp(6) not null,
        id uuid not null,
        tour_instance_id uuid,
        user_id uuid not null,
        contact_phone varchar(20) not null,
        status varchar(50) check ((status in ('PENDING','PAID','CHECKED_IN','CHECKED_OUT','CANCELLED','NO_SHOW'))),
        contact_name varchar(255) not null,
        special_requests TEXT,
        primary key (id)
    );

    create table hotel_services (
        is_active boolean,
        is_deleted boolean,
        price numeric(12,2) not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        hotel_id uuid not null,
        id uuid not null,
        category varchar(50) not null check ((category in ('FOOD','SPA','LAUNDRY','OTHER'))),
        unit varchar(50) not null,
        name varchar(100) not null,
        description TEXT,
        primary key (id)
    );

    create table hotels (
        average_rating integer,
        check_in_time time(0),
        check_out_time time(0),
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        refund_policy_id uuid,
        city_province varchar(100) not null,
        address varchar(500) not null,
        description TEXT,
        name varchar(255) not null,
        primary key (id)
    );

    create table images (
        display_order integer not null,
        is_thumbnail boolean not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        entity_id uuid not null,
        id uuid not null,
        entity_type varchar(50) not null check ((entity_type in ('HOTEL','ROOM_TYPE','TOUR','TOUR_ITINERARY','USER','DRIVER'))),
        url varchar(500) not null,
        public_id varchar(255),
        primary key (id)
    );

    create table payment_transactions (
        amount numeric(12,2) not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        booking_id uuid,
        id uuid not null,
        user_id uuid not null,
        booking_type varchar(50) check ((booking_type in ('TOUR_BOOKING','HOTEL_BOOKING','COACH_BOOKING'))),
        payment_method varchar(50) check ((payment_method in ('VNPAY','MOMO','CASH'))),
        status varchar(50) check ((status in ('PENDING','SUCCESS','FAILED','REFUNDED'))),
        transaction_type varchar(50) not null check ((transaction_type in ('PAYMENT','REFUND'))),
        transaction_reference varchar(255),
        primary key (id)
    );

    create table receptionists (
        hotel_id uuid not null,
        id uuid not null,
        employee_code varchar(50) not null unique,
        shift_type varchar(50) check ((shift_type in ('MORNING','EVENING','NIGHT'))),
        primary key (id)
    );

    create table refresh_tokens (
        revoked boolean not null,
        created_at timestamp(6) not null,
        expiry_date timestamp(6) with time zone not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        user_id uuid not null,
        token varchar(2048) not null unique,
        primary key (id)
    );

    create table refund_policies (
        is_deleted boolean not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        service_type varchar(50) not null check ((service_type in ('TOUR','HOTEL','COACH'))),
        name varchar(255) not null,
        primary key (id)
    );

    create table refund_policy_rules (
        refund_percentage numeric(5,2) not null,
        time_before integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        time_unit varchar(10) not null check ((time_unit in ('HOURS','DAYS'))),
        id uuid not null,
        refund_policy_id uuid not null,
        primary key (id)
    );

    create table refund_requests (
        actual_refunded numeric(12,2),
        requested_amount numeric(12,2) not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        payment_transaction_id uuid not null,
        processed_by_id uuid,
        user_id uuid not null,
        account_number varchar(50),
        status varchar(50) check ((status in ('PENDING','COMPLETED','REJECTED'))),
        account_holder_name varchar(100),
        bank_name varchar(100),
        customer_reason TEXT,
        reject_reason TEXT,
        primary key (id)
    );

    create table reviews (
        average_rating integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        booking_id uuid not null,
        id uuid not null,
        target_id uuid not null,
        user_id uuid not null,
        booking_type varchar(50) not null check ((booking_type in ('TOUR_BOOKING','HOTEL_BOOKING','COACH_BOOKING'))),
        target_type varchar(50) not null check ((target_type in ('TOUR','HOTEL','ROUTE'))),
        content TEXT,
        primary key (id)
    );

    create table room_assignments (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        hotel_booking_detail_id uuid not null,
        id uuid not null,
        room_id uuid not null,
        primary key (id)
    );

    create table room_type_amenities (
        amenity_id uuid not null,
        room_type_id uuid not null,
        primary key (amenity_id, room_type_id)
    );

    create table room_types (
        area integer not null,
        base_price numeric(12,2) not null,
        capacity_adults integer not null,
        capacity_children integer,
        is_deleted boolean,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        hotel_id uuid not null,
        id uuid not null,
        bed_type varchar(50) not null check ((bed_type in ('SINGLE','DOUBLE','TWIN'))),
        description TEXT,
        name varchar(255) not null,
        primary key (id)
    );

    create table rooms (
        floor integer not null,
        is_deleted boolean,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        hotel_id uuid not null,
        id uuid not null,
        room_type_id uuid not null,
        status varchar(20) check ((status in ('AVAILABLE','OCCUPIED','CLEANING','MAINTENANCE'))),
        room_number varchar(50) not null,
        primary key (id),
        unique (hotel_id, room_number)
    );

    create table routes (
        average_rating float(53),
        base_price numeric(12,2) not null,
        distance_km numeric(6,2),
        estimated_hours numeric(4,1),
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        destination_destination_id uuid not null,
        id uuid not null,
        origin_destination_id uuid not null,
        refund_policy_id uuid,
        primary key (id)
    );

    create table seat_layout_items (
        column_number integer not null,
        row_number integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        seat_name varchar(10) not null,
        id uuid not null,
        seat_layout_id uuid not null,
        position varchar(20) not null check ((position in ('FRONT','MIDDLE','BACK'))),
        tier varchar(20) not null check ((tier in ('UPPER','LOWER'))),
        primary key (id)
    );

    create table seat_layouts (
        total_seats integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        coach_type varchar(50) not null check ((coach_type in ('SEAT','BED','LIMOUSINE'))),
        name varchar(100) not null,
        primary key (id)
    );

    create table stations (
        latitude numeric(10,8),
        longitude numeric(11,8),
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        destination_id uuid not null,
        id uuid not null,
        address varchar(500) not null,
        name varchar(255) not null,
        primary key (id)
    );

    create table tour_bookings (
        price_per_adult_at_booking numeric(12,2) not null,
        price_per_child_at_booking numeric(12,2) not null,
        total_price numeric(12,2) not null,
        created_at timestamp(6) not null,
        payment_deadline timestamp(6),
        updated_at timestamp(6) not null,
        id uuid not null,
        tour_instance_id uuid not null,
        user_id uuid not null,
        status varchar(50) check ((status in ('PENDING','PAID','CHECKED_IN','CHECKED_OUT','CANCELLED','NO_SHOW'))),
        special_requests TEXT,
        primary key (id)
    );

    create table tour_incidents (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        reporter_id uuid not null,
        tour_instance_id uuid not null,
        severity varchar(20) not null check ((severity in ('LOW','MEDIUM','HIGH','CRITICAL'))),
        status varchar(20) not null check ((status in ('PENDING','PROCESSING','RESOLVED','CLOSED'))),
        description TEXT not null,
        title varchar(255) not null,
        primary key (id)
    );

    create table tour_instances (
        current_participants integer,
        end_date date not null,
        start_date date not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        coach_id uuid,
        coordinator_id uuid not null,
        driver_id uuid,
        guide_id uuid,
        hotel_booking_id uuid,
        id uuid not null,
        tour_id uuid not null,
        status varchar(50) check ((status in ('PLANNING','OPEN','FULL','IN_PROGRESS','COMPLETED','CANCELLED'))),
        primary key (id)
    );

    create table tour_itineraries (
        day_number integer not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        tour_id uuid not null,
        description TEXT not null,
        title varchar(255) not null,
        primary key (id)
    );

    create table tourists (
        date_of_birth date,
        gender varchar(10) check ((gender in ('MALE','FEMALE','OTHER'))),
        id uuid not null,
        passport_number varchar(50) unique,
        primary key (id)
    );

    create table tours (
        average_rating float(53),
        duration_days integer,
        is_custom boolean not null,
        max_participants integer not null check ((max_participants>=10) and (max_participants<=30)),
        min_participants integer not null check ((min_participants>=10) and (min_participants<=30)),
        price_per_adult numeric(12,2) not null,
        price_per_child numeric(12,2) not null,
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        coordinator_id uuid not null,
        destination_id uuid not null,
        hotel_id uuid,
        id uuid not null,
        refund_policy_id uuid,
        requested_by_user_id uuid,
        pickup_location varchar(500) not null,
        description TEXT,
        name varchar(255) not null,
        primary key (id)
    );

    create table users (
        created_at timestamp(6) not null,
        updated_at timestamp(6) not null,
        id uuid not null,
        auth_provider varchar(20) not null check ((auth_provider in ('LOCAL','GOOGLE'))),
        cometchat_uid varchar(100) unique,
        avatar_url varchar(500),
        avatar_public_id varchar(255),
        email varchar(255) unique,
        full_name varchar(255) not null,
        password_hashed varchar(255),
        phone_number varchar(255) unique,
        role varchar(255) not null check ((role in ('TOURIST','RECEPTIONIST','COORDINATOR','GUIDE','ADMIN'))),
        status varchar(255) not null check ((status in ('ACTIVE','PENDING','DELETED','BANNED'))),
        primary key (id)
    );

    alter table if exists add_on_orders
       add constraint fk_add_on_orders_hotel_booking_id
       foreign key (hotel_booking_id)
       references hotel_bookings;

    alter table if exists add_on_orders
       add constraint fk_add_on_orders_hotel_service_id
       foreign key (hotel_service_id)
       references hotel_services;

    alter table if exists admins
       add constraint fk_admins_id
       foreign key (id)
       references users;

    alter table if exists chat_sessions
       add constraint fk_chat_sessions_coordinator_id
       foreign key (coordinator_id)
       references coordinators;

    alter table if exists chat_sessions
       add constraint fk_chat_sessions_tour_id
       foreign key (tour_id)
       references tours;

    alter table if exists chat_sessions
       add constraint fk_chat_sessions_tour_instance_id
       foreign key (tour_instance_id)
       references tour_instances;

    alter table if exists chat_sessions
       add constraint fk_chat_sessions_user_id
       foreign key (user_id)
       references users;

    alter table if exists coach_booking_seats
       add constraint fk_coach_booking_seats_coach_booking_id
       foreign key (coach_booking_id)
       references coach_bookings;

    alter table if exists coach_booking_seats
       add constraint fk_coach_booking_seats_seat_layout_item_id
       foreign key (seat_layout_item_id)
       references seat_layout_items;

    alter table if exists coach_bookings
       add constraint fk_coach_bookings_coach_trip_id
       foreign key (coach_trip_id)
       references coach_trips;

    alter table if exists coach_bookings
       add constraint fk_coach_bookings_user_id
       foreign key (user_id)
       references users;

    alter table if exists coach_trips
       add constraint fk_coach_trips_coach_id
       foreign key (coach_id)
       references coaches;

    alter table if exists coach_trips
       add constraint fk_coach_trips_coordinator_id
       foreign key (coordinator_id)
       references coordinators;

    alter table if exists coach_trips
       add constraint fk_coach_trips_driver_id
       foreign key (driver_id)
       references drivers;

    alter table if exists coach_trips
       add constraint fk_coach_trips_route_id
       foreign key (route_id)
       references routes;

    alter table if exists coaches
       add constraint fk_coaches_seat_layout_id
       foreign key (seat_layout_id)
       references seat_layouts;

    alter table if exists coordinators
       add constraint fk_coordinators_id
       foreign key (id)
       references users;

    alter table if exists guides
       add constraint fk_guides_id
       foreign key (id)
       references users;

    alter table if exists hotel_amenities
       add constraint fk_hotel_amenities_amenity_id
       foreign key (amenity_id)
       references amenities;

    alter table if exists hotel_amenities
       add constraint fk_hotel_amenities_hotel_id
       foreign key (hotel_id)
       references hotels;

    alter table if exists hotel_booking_details
       add constraint fk_hotel_booking_details_hotel_booking_id
       foreign key (hotel_booking_id)
       references hotel_bookings;

    alter table if exists hotel_booking_details
       add constraint fk_hotel_booking_details_room_type_id
       foreign key (room_type_id)
       references room_types;

    alter table if exists hotel_bookings
       add constraint fk_hotel_bookings_tour_instance_id
       foreign key (tour_instance_id)
       references tour_instances;

    alter table if exists hotel_bookings
       add constraint fk_hotel_bookings_user_id
       foreign key (user_id)
       references users;

    alter table if exists hotel_services
       add constraint fk_hotel_services_hotel_id
       foreign key (hotel_id)
       references hotels;

    alter table if exists hotels
       add constraint fk_hotels_refund_policy_id
       foreign key (refund_policy_id)
       references refund_policies;

    alter table if exists payment_transactions
       add constraint fk_payment_transactions_user_id
       foreign key (user_id)
       references users;

    alter table if exists receptionists
       add constraint fk_receptionists_hotel_id
       foreign key (hotel_id)
       references hotels;

    alter table if exists receptionists
       add constraint fk_receptionists_id
       foreign key (id)
       references users;

    alter table if exists refresh_tokens
       add constraint fk_refresh_tokens_user_id
       foreign key (user_id)
       references users;

    alter table if exists refund_policy_rules
       add constraint fk_refund_policy_rules_refund_policy_id
       foreign key (refund_policy_id)
       references refund_policies;

    alter table if exists refund_requests
       add constraint fk_refund_requests_payment_transaction_id
       foreign key (payment_transaction_id)
       references payment_transactions;

    alter table if exists refund_requests
       add constraint fk_refund_requests_processed_by_id
       foreign key (processed_by_id)
       references coordinators;

    alter table if exists refund_requests
       add constraint fk_refund_requests_user_id
       foreign key (user_id)
       references users;

    alter table if exists reviews
       add constraint fk_reviews_user_id
       foreign key (user_id)
       references users;

    alter table if exists room_assignments
       add constraint fk_room_assignments_hotel_booking_detail_id
       foreign key (hotel_booking_detail_id)
       references hotel_booking_details;

    alter table if exists room_assignments
       add constraint fk_room_assignments_room_id
       foreign key (room_id)
       references rooms;

    alter table if exists room_type_amenities
       add constraint fk_room_type_amenities_amenity_id
       foreign key (amenity_id)
       references amenities;

    alter table if exists room_type_amenities
       add constraint fk_room_type_amenities_room_type_id
       foreign key (room_type_id)
       references room_types;

    alter table if exists room_types
       add constraint fk_room_types_hotel_id
       foreign key (hotel_id)
       references hotels;

    alter table if exists rooms
       add constraint fk_rooms_hotel_id
       foreign key (hotel_id)
       references hotels;

    alter table if exists rooms
       add constraint fk_rooms_room_type_id
       foreign key (room_type_id)
       references room_types;

    alter table if exists routes
       add constraint fk_routes_destination_destination_id
       foreign key (destination_destination_id)
       references destinations;

    alter table if exists routes
       add constraint fk_routes_origin_destination_id
       foreign key (origin_destination_id)
       references destinations;

    alter table if exists routes
       add constraint fk_routes_refund_policy_id
       foreign key (refund_policy_id)
       references refund_policies;

    alter table if exists seat_layout_items
       add constraint fk_seat_layout_items_seat_layout_id
       foreign key (seat_layout_id)
       references seat_layouts;

    alter table if exists stations
       add constraint fk_stations_destination_id
       foreign key (destination_id)
       references destinations;

    alter table if exists tour_bookings
       add constraint fk_tour_bookings_tour_instance_id
       foreign key (tour_instance_id)
       references tour_instances;

    alter table if exists tour_bookings
       add constraint fk_tour_bookings_user_id
       foreign key (user_id)
       references users;

    alter table if exists tour_incidents
       add constraint fk_tour_incidents_reporter_id
       foreign key (reporter_id)
       references users;

    alter table if exists tour_incidents
       add constraint fk_tour_incidents_tour_instance_id
       foreign key (tour_instance_id)
       references tour_instances;

    alter table if exists tour_instances
       add constraint fk_tour_instances_coach_id
       foreign key (coach_id)
       references coaches;

    alter table if exists tour_instances
       add constraint fk_tour_instances_coordinator_id
       foreign key (coordinator_id)
       references coordinators;

    alter table if exists tour_instances
       add constraint fk_tour_instances_driver_id
       foreign key (driver_id)
       references drivers;

    alter table if exists tour_instances
       add constraint fk_tour_instances_guide_id
       foreign key (guide_id)
       references guides;

    alter table if exists tour_instances
       add constraint fk_tour_instances_hotel_booking_id
       foreign key (hotel_booking_id)
       references hotel_bookings;

    alter table if exists tour_instances
       add constraint fk_tour_instances_tour_id
       foreign key (tour_id)
       references tours;

    alter table if exists tour_itineraries
       add constraint fk_tour_itineraries_tour_id
       foreign key (tour_id)
       references tours;

    alter table if exists tourists
       add constraint fk_tourists_id
       foreign key (id)
       references users;

    alter table if exists tours
       add constraint fk_tours_coordinator_id
       foreign key (coordinator_id)
       references coordinators;

    alter table if exists tours
       add constraint fk_tours_destination_id
       foreign key (destination_id)
       references destinations;

    alter table if exists tours
       add constraint fk_tours_hotel_id
       foreign key (hotel_id)
       references hotels;

    alter table if exists tours
       add constraint fk_tours_refund_policy_id
       foreign key (refund_policy_id)
       references refund_policies;

    alter table if exists tours
       add constraint fk_tours_requested_by_user_id
       foreign key (requested_by_user_id)
       references users;
