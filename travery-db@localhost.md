classDiagram
direction BT
class add_on_orders {
   integer quantity
   numeric(12,2) total_price
   timestamp(6) created_at
   timestamp(6) scheduled_time
   timestamp(6) updated_at
   uuid hotel_booking_id
   uuid hotel_service_id
   varchar(50) status
   uuid id
}
class admins {
   uuid id
}
class amenities {
   boolean is_active
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(50) type
   varchar(100) name
   varchar(255) icon_public_id
   varchar(255) icon_url
   uuid id
}
class booking_members {
   date date_of_birth
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid booking_id
   varchar(20) member_type
   varchar(50) attendance_status
   varchar(50) booking_type
   varchar(50) identity_number
   varchar(100) full_name
   uuid id
}
class chat_sessions {
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid coordinator_id
   uuid tour_id
   uuid tour_instance_id
   uuid user_id
   varchar(20) status
   varchar(100) cometchat_guid
   uuid id
}
class coach_booking_seats {
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid coach_booking_id
   uuid seat_layout_item_id
   uuid id
}
class coach_bookings {
   numeric(12,2) base_price
   numeric(12,2) total_price
   timestamp(6) created_at
   timestamp(6) payment_deadline
   timestamp(6) updated_at
   uuid coach_trip_id
   uuid user_id
   varchar(20) contact_phone
   varchar(50) status
   varchar(100) contact_name
   uuid id
}
class coach_trips {
   timestamp(6) arrival_time
   timestamp(6) created_at
   timestamp(6) departure_time
   timestamp(6) updated_at
   uuid coach_id
   uuid coordinator_id
   uuid driver_id
   uuid guide_id
   uuid route_id
   varchar(50) status
   uuid id
}
class coaches {
   integer capacity
   boolean is_deleted
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid seat_layout_id
   varchar(20) license_plate
   varchar(20) status
   varchar(50) coach_type
   uuid id
}
class coordinators {
   varchar(50) department
   varchar(50) employee_code
   uuid id
}
class destinations {
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(50) code
   varchar(255) name
   uuid id
}
class drivers {
   boolean is_deleted
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(20) phone_number
   varchar(20) status
   varchar(50) license_number
   varchar(100) full_name
   varchar(500) avatar_url
   varchar(255) avatar_public_id
   uuid id
}
class flyway_schema_history {
   varchar(50) version
   varchar(200) description
   varchar(20) type
   varchar(1000) script
   integer checksum
   varchar(100) installed_by
   timestamp installed_on
   integer execution_time
   boolean success
   integer installed_rank
}
class guides {
   integer years_experience
   varchar(50) employee_code
   varchar(100) guide_license
   jsonb languages
   uuid id
}
class hotel_amenities {
   uuid amenity_id
   uuid hotel_id
}
class hotel_booking_details {
   numeric(12,2) price_at_booking
   integer quantity
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid hotel_booking_id
   uuid room_type_id
   uuid id
}
class hotel_bookings {
   date end_date
   date start_date
   numeric(12,2) total_price
   timestamp(6) actual_check_in_time
   timestamp(6) actual_check_out_time
   timestamp(6) created_at
   timestamp(6) payment_deadline
   timestamp(6) updated_at
   uuid tour_instance_id
   uuid user_id
   varchar(20) contact_phone
   varchar(50) status
   varchar(255) contact_name
   text special_requests
   uuid id
}
class hotel_services {
   boolean is_active
   boolean is_deleted
   numeric(12,2) price
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid hotel_id
   varchar(50) category
   varchar(50) unit
   varchar(100) name
   text description
   uuid id
}
class hotels {
   double precision average_rating
   integer review_count
   time(0) check_in_time
   time(0) check_out_time
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid refund_policy_id
   varchar(100) city_province
   varchar(500) address
   text description
   varchar(255) name
   uuid id
}
class images {
   integer display_order
   boolean is_thumbnail
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid entity_id
   varchar(50) entity_type
   varchar(500) url
   varchar(255) public_id
   uuid id
}
class payment_transactions {
   numeric(12,2) amount
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid booking_id
   uuid user_id
   varchar(50) booking_type
   varchar(50) payment_method
   varchar(50) status
   varchar(50) transaction_type
   varchar(255) transaction_reference
   uuid id
}
class receptionists {
   uuid hotel_id
   varchar(50) employee_code
   varchar(50) shift_type
   uuid id
}
class refresh_tokens {
   boolean revoked
   timestamp(6) created_at
   timestamp(6) with time zone expiry_date
   timestamp(6) updated_at
   uuid user_id
   varchar(2048) token
   uuid id
}
class refund_policies {
   boolean is_deleted
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(50) service_type
   varchar(255) name
   uuid id
}
class refund_policy_rules {
   numeric(5,2) refund_percentage
   integer time_before
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(10) time_unit
   uuid refund_policy_id
   uuid id
}
class refund_requests {
   numeric(12,2) actual_refunded
   numeric(12,2) requested_amount
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid payment_transaction_id
   uuid processed_by_id
   uuid user_id
   varchar(50) account_number
   varchar(50) status
   varchar(100) account_holder_name
   varchar(100) bank_name
   text customer_reason
   text reject_reason
   uuid id
}
class reviews {
   timestamp(6) updated_at
   uuid booking_id
   uuid id
   uuid target_id
   uuid user_id
   varchar(50) booking_type
   varchar(50) target_type
   text content
   integer average_rating
   timestamp(6) created_at
}
class room_assignments {
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid hotel_booking_detail_id
   uuid room_id
   uuid id
}
class room_type_amenities {
   uuid amenity_id
   uuid room_type_id
}
class room_types {
   integer area
   numeric(12,2) base_price
   integer capacity_adults
   integer capacity_children
   boolean is_deleted
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid hotel_id
   varchar(50) bed_type
   text description
   varchar(255) name
   uuid id
}
class rooms {
   integer floor
   boolean is_deleted
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid hotel_id
   uuid room_type_id
   varchar(20) status
   varchar(50) room_number
   uuid id
}
class routes {
   double precision average_rating
   boolean is_deleted
   integer review_count
   numeric(12,2) base_price
   numeric(6,2) distance_km
   numeric(4,1) estimated_hours
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid destination_destination_id
   uuid origin_destination_id
   uuid refund_policy_id
   uuid id
}
class seat_layout_items {
   integer column_number
   integer row_number
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(10) seat_name
   uuid seat_layout_id
   varchar(20) position
   varchar(20) tier
   uuid id
}
class seat_layouts {
   integer total_seats
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(50) coach_type
   varchar(100) name
   uuid id
}
class stations {
   boolean is_deleted
   numeric(10,8) latitude
   numeric(11,8) longitude
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid destination_id
   varchar(500) address
   varchar(255) name
   uuid id
}
class tour_bookings {
   numeric(12,2) price_per_adult_at_booking
   numeric(12,2) price_per_child_at_booking
   numeric(12,2) total_price
   timestamp(6) created_at
   timestamp(6) payment_deadline
   timestamp(6) updated_at
   uuid tour_instance_id
   uuid user_id
   varchar(50) status
   text special_requests
   uuid id
}
class tour_incidents {
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid reporter_id
   uuid tour_instance_id
   varchar(20) severity
   varchar(20) status
   text description
   varchar(255) title
   uuid id
}
class tour_instances {
   integer current_participants
   date end_date
   date start_date
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid coach_id
   uuid coordinator_id
   uuid driver_id
   uuid guide_id
   uuid hotel_booking_id
   uuid tour_id
   varchar(50) status
   uuid id
}
class tour_itineraries {
   integer day_number
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid tour_id
   text description
   varchar(255) title
   uuid id
}
class tourists {
   date date_of_birth
   varchar(10) gender
   varchar(50) passport_number
   uuid id
}
class tours {
   double precision average_rating
   integer review_count
   integer duration_days
   boolean is_custom
   integer max_participants
   integer min_participants
   numeric(12,2) price_per_adult
   numeric(12,2) price_per_child
   timestamp(6) created_at
   timestamp(6) updated_at
   uuid coordinator_id
   uuid destination_id
   uuid hotel_id
   uuid refund_policy_id
   uuid requested_by_user_id
   varchar(500) pickup_location
   text description
   varchar(255) name
   uuid id
}
class users {
   timestamp(6) created_at
   timestamp(6) updated_at
   varchar(20) auth_provider
   varchar(100) cometchat_uid
   varchar(500) avatar_url
   varchar(255) avatar_public_id
   varchar(255) email
   varchar(255) full_name
   varchar(255) password_hashed
   varchar(255) phone_number
   varchar(255) role
   varchar(255) status
   uuid id
}

add_on_orders  -->  hotel_bookings : hotel_booking_id:id
add_on_orders  -->  hotel_services : hotel_service_id:id
admins  -->  users : id
chat_sessions  -->  coordinators : coordinator_id:id
chat_sessions  -->  tour_instances : tour_instance_id:id
chat_sessions  -->  tours : tour_id:id
chat_sessions  -->  users : user_id:id
coach_booking_seats  -->  coach_bookings : coach_booking_id:id
coach_booking_seats  -->  seat_layout_items : seat_layout_item_id:id
coach_bookings  -->  coach_trips : coach_trip_id:id
coach_bookings  -->  users : user_id:id
coach_trips  -->  coaches : coach_id:id
coach_trips  -->  coordinators : coordinator_id:id
coach_trips  -->  drivers : driver_id:id
coach_trips  -->  guides : guide_id:id
coach_trips  -->  routes : route_id:id
coaches  -->  seat_layouts : seat_layout_id:id
coordinators  -->  users : id
guides  -->  users : id
hotel_amenities  -->  amenities : amenity_id:id
hotel_amenities  -->  hotels : hotel_id:id
hotel_booking_details  -->  hotel_bookings : hotel_booking_id:id
hotel_booking_details  -->  room_types : room_type_id:id
hotel_bookings  -->  tour_instances : tour_instance_id:id
hotel_bookings  -->  users : user_id:id
hotel_services  -->  hotels : hotel_id:id
hotels  -->  refund_policies : refund_policy_id:id
payment_transactions  -->  users : user_id:id
receptionists  -->  hotels : hotel_id:id
receptionists  -->  users : id
refresh_tokens  -->  users : user_id:id
refund_policy_rules  -->  refund_policies : refund_policy_id:id
refund_requests  -->  coordinators : processed_by_id:id
refund_requests  -->  payment_transactions : payment_transaction_id:id
refund_requests  -->  users : user_id:id
reviews  -->  users : user_id:id
room_assignments  -->  hotel_booking_details : hotel_booking_detail_id:id
room_assignments  -->  rooms : room_id:id
room_type_amenities  -->  amenities : amenity_id:id
room_type_amenities  -->  room_types : room_type_id:id
room_types  -->  hotels : hotel_id:id
rooms  -->  hotels : hotel_id:id
rooms  -->  room_types : room_type_id:id
routes  -->  destinations : destination_destination_id:id
routes  -->  destinations : origin_destination_id:id
routes  -->  refund_policies : refund_policy_id:id
seat_layout_items  -->  seat_layouts : seat_layout_id:id
stations  -->  destinations : destination_id:id
tour_bookings  -->  tour_instances : tour_instance_id:id
tour_bookings  -->  users : user_id:id
tour_incidents  -->  tour_instances : tour_instance_id:id
tour_incidents  -->  users : reporter_id:id
tour_instances  -->  coaches : coach_id:id
tour_instances  -->  coordinators : coordinator_id:id
tour_instances  -->  drivers : driver_id:id
tour_instances  -->  guides : guide_id:id
tour_instances  -->  hotel_bookings : hotel_booking_id:id
tour_instances  -->  tours : tour_id:id
tour_itineraries  -->  tours : tour_id:id
tourists  -->  users : id
tours  -->  coordinators : coordinator_id:id
tours  -->  destinations : destination_id:id
tours  -->  hotels : hotel_id:id
tours  -->  refund_policies : refund_policy_id:id
tours  -->  users : requested_by_user_id:id
