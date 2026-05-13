-- =============================================
-- Travery Backend - Init Schema (V1)
-- Generated from Hibernate Entities
-- =============================================

-- =============================================
-- 1. CREATE TABLES
-- =============================================

create table add_on_orders (
    quantity integer not null,
    total_price numeric(12,2) not null,
    created_at timestamp(6) not null,
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
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
    type varchar(50) not null check ((type in ('HOTEL_AMENITY','ROOM_AMENITY'))),
    name varchar(100) not null unique,
    icon_url varchar(255),
    primary key (id)
);

create table booking_members (
    date_of_birth date,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    booking_id uuid not null,
    id uuid not null,
    booking_type varchar(50) not null check ((booking_type in ('TOUR_BOOKING','HOTEL_BOOKING','COACH_BOOKING'))),
    passport_number varchar(50) not null,
    full_name varchar(100) not null,
    primary key (id)
);

create table chat_sessions (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    coordinator_id uuid,
    id uuid not null,
    tour_id uuid,
    user_id uuid not null,
    status varchar(20) check ((status in ('OPEN','CLOSED'))),
    cometchat_guid varchar(100) not null unique,
    primary key (id)
);

create table coach_bookings (
    total_price numeric(12,2) not null,
    created_at timestamp(6) not null,
    payment_deadline timestamp(6),
    updated_at timestamp(6) not null,
    coach_trip_id uuid not null,
    id uuid not null,
    user_id uuid not null,
    status varchar(50) check ((status in ('PENDING','PAID','CHECKED_IN','CHECKED_OUT','CANCELLED'))),
    primary key (id)
);

create table coach_seats (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    seat_name varchar(10) not null,
    coach_id uuid not null,
    id uuid not null,
    position varchar(20) not null check ((position in ('FRONT','MIDDLE','BACK'))),
    tier varchar(20) not null check ((tier in ('UPPER','LOWER'))),
    primary key (id)
);

create table coach_tickets (
    price_at_booking numeric(12,2) not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    coach_booking_id uuid not null,
    coach_seat_id uuid not null,
    id uuid not null,
    passenger_phone varchar(20),
    passenger_name varchar(100),
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
    status varchar(50) check ((status in ('SCHEDULED','IN_PROGRESS','COMPLETED','CANCELLED'))),
    primary key (id)
);

create table coaches (
    capacity integer not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
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

create table drivers (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
    phone_number varchar(20) not null unique,
    status varchar(20) check ((status in ('AVAILABLE','ON_TRIP','ON_LEAVE'))),
    license_number varchar(50) not null unique,
    full_name varchar(100) not null,
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
    end_date date not null,
    price_at_booking numeric(12,2) not null,
    quantity integer not null,
    start_date date not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    hotel_booking_id uuid not null,
    id uuid not null,
    room_type_id uuid not null,
    primary key (id)
);

create table hotel_bookings (
    total_price numeric(12,2) not null,
    created_at timestamp(6) not null,
    payment_deadline timestamp(6),
    updated_at timestamp(6) not null,
    id uuid not null,
    tour_instance_id uuid,
    user_id uuid not null,
    status varchar(50) check ((status in ('PENDING','PAID','CHECKED_IN','CHECKED_OUT','CANCELLED'))),
    primary key (id)
);

create table hotel_services (
    is_active boolean,
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
    check_in_time time(0),
    check_out_time time(0),
    latitude numeric(10,8) not null,
    longitude numeric(11,8) not null,
    star_rating integer not null,
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
    entity_type varchar(50) not null check ((entity_type in ('HOTEL','ROOM_TYPE','TOUR','TOUR_ITINERARY'))),
    url varchar(500) not null,
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
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
    service_type varchar(50) not null check ((service_type in ('TOUR','HOTEL','COACH'))),
    name varchar(255) not null,
    primary key (id)
);

create table refund_policy_rules (
    days_before integer not null,
    refund_percentage numeric(5,2) not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
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
    status varchar(50) check ((status in ('PENDING','PROCESSING','COMPLETED','REJECTED'))),
    customer_reason TEXT,
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
    base_price numeric(12,2) not null,
    capacity_adults integer not null,
    capacity_children integer,
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
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    hotel_id uuid not null,
    id uuid not null,
    room_type_id uuid not null,
    status varchar(20) check ((status in ('AVAILABLE','OCCUPIED','MAINTENANCE'))),
    room_number varchar(50) not null,
    primary key (id),
    unique (hotel_id, room_number)
);

create table routes (
    base_price numeric(12,2) not null,
    distance_km numeric(6,2),
    estimated_hours numeric(4,1),
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    destination_station_id uuid not null,
    id uuid not null,
    origin_station_id uuid not null,
    refund_policy_id uuid,
    primary key (id)
);

create table stations (
    latitude numeric(10,8),
    longitude numeric(11,8),
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
    city_province varchar(100) not null,
    address varchar(500) not null,
    name varchar(255) not null,
    primary key (id)
);

create table tour_bookings (
    total_price numeric(12,2) not null,
    created_at timestamp(6) not null,
    payment_deadline timestamp(6),
    updated_at timestamp(6) not null,
    id uuid not null,
    tour_instance_id uuid not null,
    user_id uuid not null,
    status varchar(50) check ((status in ('PENDING','PAID','CHECKED_IN','CHECKED_OUT','CANCELLED'))),
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

create table destinations (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    id uuid not null,
    code varchar(50) not null unique,
    name varchar(255) not null,
    region varchar(20) not null check ((region in ('NORTH','CENTRAL','SOUTH'))),
    image_url varchar(500),
    description TEXT,
    primary key (id)
);

create table tours (
    average_rating double precision not null default 0.0,
    duration_days integer not null default 1,
    is_custom boolean not null,
    max_participants integer not null default 30,
    min_participants integer not null default 10,
    constraint check_participants check (min_participants >= 10 and max_participants <= 30 and max_participants >= min_participants),
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
    email varchar(255) unique,
    full_name varchar(255) not null,
    password_hashed varchar(255),
    phone_number varchar(255) unique,
    role varchar(255) not null check ((role in ('TOURIST','RECEPTIONIST','COORDINATOR','GUIDE','ADMIN'))),
    status varchar(255) not null check ((status in ('ACTIVE','PENDING','DELETED','BANNED'))),
    primary key (id)
);

-- =============================================
-- 2. FOREIGN KEY CONSTRAINTS
-- =============================================

-- add_on_orders
alter table add_on_orders
    add constraint fk_add_on_orders_hotel_booking
    foreign key (hotel_booking_id) references hotel_bookings;

alter table add_on_orders
    add constraint fk_add_on_orders_hotel_service
    foreign key (hotel_service_id) references hotel_services;

-- admins (inheritance → users)
alter table admins
    add constraint fk_admins_user
    foreign key (id) references users;

-- chat_sessions
alter table chat_sessions
    add constraint fk_chat_sessions_coordinator
    foreign key (coordinator_id) references coordinators;

alter table chat_sessions
    add constraint fk_chat_sessions_tour
    foreign key (tour_id) references tours;

alter table chat_sessions
    add constraint fk_chat_sessions_user
    foreign key (user_id) references users;

-- coach_bookings
alter table coach_bookings
    add constraint fk_coach_bookings_coach_trip
    foreign key (coach_trip_id) references coach_trips;

alter table coach_bookings
    add constraint fk_coach_bookings_user
    foreign key (user_id) references users;

-- coach_seats
alter table coach_seats
    add constraint fk_coach_seats_coach
    foreign key (coach_id) references coaches;

-- coach_tickets
alter table coach_tickets
    add constraint fk_coach_tickets_coach_booking
    foreign key (coach_booking_id) references coach_bookings;

alter table coach_tickets
    add constraint fk_coach_tickets_coach_seat
    foreign key (coach_seat_id) references coach_seats;

-- coach_trips
alter table coach_trips
    add constraint fk_coach_trips_coach
    foreign key (coach_id) references coaches;

alter table coach_trips
    add constraint fk_coach_trips_coordinator
    foreign key (coordinator_id) references coordinators;

alter table coach_trips
    add constraint fk_coach_trips_driver
    foreign key (driver_id) references drivers;

alter table coach_trips
    add constraint fk_coach_trips_route
    foreign key (route_id) references routes;

-- coordinators (inheritance → users)
alter table coordinators
    add constraint fk_coordinators_user
    foreign key (id) references users;

-- guides (inheritance → users)
alter table guides
    add constraint fk_guides_user
    foreign key (id) references users;

-- hotel_amenities (join table)
alter table hotel_amenities
    add constraint fk_hotel_amenities_amenity
    foreign key (amenity_id) references amenities;

alter table hotel_amenities
    add constraint fk_hotel_amenities_hotel
    foreign key (hotel_id) references hotels;

-- hotel_booking_details
alter table hotel_booking_details
    add constraint fk_hotel_booking_details_hotel_booking
    foreign key (hotel_booking_id) references hotel_bookings;

alter table hotel_booking_details
    add constraint fk_hotel_booking_details_room_type
    foreign key (room_type_id) references room_types;

-- hotel_bookings
alter table hotel_bookings
    add constraint fk_hotel_bookings_tour_instance
    foreign key (tour_instance_id) references tour_instances;

alter table hotel_bookings
    add constraint fk_hotel_bookings_user
    foreign key (user_id) references users;

-- hotel_services
alter table hotel_services
    add constraint fk_hotel_services_hotel
    foreign key (hotel_id) references hotels;

-- payment_transactions
alter table payment_transactions
    add constraint fk_payment_transactions_user
    foreign key (user_id) references users;

-- receptionists
alter table receptionists
    add constraint fk_receptionists_hotel
    foreign key (hotel_id) references hotels;

alter table receptionists
    add constraint fk_receptionists_user
    foreign key (id) references users;

-- refresh_tokens
alter table refresh_tokens
    add constraint fk_refresh_tokens_user
    foreign key (user_id) references users;

-- refund_policy_rules
alter table refund_policy_rules
    add constraint fk_refund_policy_rules_refund_policy
    foreign key (refund_policy_id) references refund_policies;

-- refund_requests
alter table refund_requests
    add constraint fk_refund_requests_payment_transaction
    foreign key (payment_transaction_id) references payment_transactions;

alter table refund_requests
    add constraint fk_refund_requests_processed_by
    foreign key (processed_by_id) references coordinators;

alter table refund_requests
    add constraint fk_refund_requests_user
    foreign key (user_id) references users;

-- reviews
alter table reviews
    add constraint fk_reviews_user
    foreign key (user_id) references users;

-- room_assignments
alter table room_assignments
    add constraint fk_room_assignments_hotel_booking_detail
    foreign key (hotel_booking_detail_id) references hotel_booking_details;

alter table room_assignments
    add constraint fk_room_assignments_room
    foreign key (room_id) references rooms;

-- room_type_amenities (join table)
alter table room_type_amenities
    add constraint fk_room_type_amenities_amenity
    foreign key (amenity_id) references amenities;

alter table room_type_amenities
    add constraint fk_room_type_amenities_room_type
    foreign key (room_type_id) references room_types;

-- room_types
alter table room_types
    add constraint fk_room_types_hotel
    foreign key (hotel_id) references hotels;

-- rooms
alter table rooms
    add constraint fk_rooms_hotel
    foreign key (hotel_id) references hotels;

alter table rooms
    add constraint fk_rooms_room_type
    foreign key (room_type_id) references room_types;

-- routes
alter table routes
    add constraint fk_routes_destination_station
    foreign key (destination_station_id) references stations;

alter table routes
    add constraint fk_routes_origin_station
    foreign key (origin_station_id) references stations;

-- tour_bookings
alter table tour_bookings
    add constraint fk_tour_bookings_tour_instance
    foreign key (tour_instance_id) references tour_instances;

alter table tour_bookings
    add constraint fk_tour_bookings_user
    foreign key (user_id) references users;



-- tour_instances
alter table tour_instances
    add constraint fk_tour_instances_coach
    foreign key (coach_id) references coaches;

alter table tour_instances
    add constraint fk_tour_instances_coordinator
    foreign key (coordinator_id) references coordinators;

alter table tour_instances
    add constraint fk_tour_instances_driver
    foreign key (driver_id) references drivers;

alter table tour_instances
    add constraint fk_tour_instances_guide
    foreign key (guide_id) references guides;

alter table tour_instances
    add constraint fk_tour_instances_hotel_booking
    foreign key (hotel_booking_id) references hotel_bookings;

alter table tour_instances
    add constraint fk_tour_instances_tour
    foreign key (tour_id) references tours;

-- tour_itineraries
alter table tour_itineraries
    add constraint fk_tour_itineraries_tour
    foreign key (tour_id) references tours;

-- tourists (inheritance → users)
alter table tourists
    add constraint fk_tourists_user
    foreign key (id) references users;

-- tours
alter table tours
    add constraint fk_tours_coordinator
    foreign key (coordinator_id) references coordinators;

alter table tours
    add constraint fk_tours_destination
    foreign key (destination_id) references destinations;

alter table tours
    add constraint fk_tours_hotel
    foreign key (hotel_id) references hotels;

alter table tours
    add constraint fk_tours_refund_policy
    foreign key (refund_policy_id) references refund_policies;

alter table tours
    add constraint fk_tours_requested_by_user
    foreign key (requested_by_user_id) references users;
