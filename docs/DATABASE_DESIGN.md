# Database Design

This document contains the final DBML version of the database design.

Important decisions:

- Rentals can exist without enquiries.
- Maintenance requests belong to rentals.
- Students and landlords are stored as user profiles linked to the users table.
- Listings belong to landlords.
- Public listings must be approved and available.

## DBML

```dbml
Enum user_role {
  STUDENT
  LANDLORD
  ADMIN
}

Enum landlord_verification_status {
  PENDING
  APPROVED
  REJECTED
}

Enum gender_preference {
  ANY
  MALE
  FEMALE
}

Enum listing_status {
  PENDING
  APPROVED
  REJECTED
  DEACTIVATED
}

Enum enquiry_status {
  PENDING
  APPROVED
  REJECTED
  CANCELLED
}

Enum maintenance_priority {
  LOW
  MEDIUM
  HIGH
  URGENT
}

Enum maintenance_status {
  OPEN
  IN_PROGRESS
  RESOLVED
  CLOSED
}

Enum rental_status {
  ACTIVE
  ENDED
  CANCELLED
}

Table users {
  id bigint [pk, increment]

  role user_role [not null, default: 'STUDENT']
  first_name varchar(40) [not null]
  middle_name varchar(40)
  last_name varchar(40) [not null]
  email varchar(255) [not null, unique]
  password_hash varchar(255) [not null]
  phone_number varchar(20) [unique]
  active boolean [not null, default: false]

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}

Table student_profiles {
  id bigint [pk, increment]
  user_id bigint [not null, unique]

  student_number varchar(20) [not null, unique]
  institution_name varchar(70) [not null]
  preferred_area varchar(100)

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}

Table landlord_profiles {
  id bigint [pk, increment]
  user_id bigint [not null, unique]

  verification_status landlord_verification_status [not null, default: 'PENDING']
  business_name varchar(100)
  id_document varchar(255) [not null]
  rejection_reason text

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}

Table locations {
  id bigint [pk, increment]

  area varchar(100) [not null]
  city varchar(100) [not null]
  address varchar(255) [not null]

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}

Table listings {
  id bigint [pk, increment]

  landlord_id bigint [not null]
  location_id bigint [not null]

  title varchar(150) [not null]
  description text [not null]
  price decimal(10,2) [not null]

  available boolean [not null, default: true]
  gender_preference gender_preference [not null, default: 'ANY']
  furnished boolean [not null, default: false]

  rules text
  status listing_status [not null, default: 'PENDING']

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}

Table listing_images {
  id bigint [pk, increment]

  listing_id bigint [not null]
  image_url varchar(500) [not null]
  is_primary boolean [not null, default: false]

  created_at timestamptz [not null, default: `now()`]
}

Table saved_listings {
  id bigint [pk, increment]

  student_id bigint [not null]
  listing_id bigint [not null]

  created_at timestamptz [not null, default: `now()`]

  Indexes {
    (student_id, listing_id) [unique]
  }
}

Table enquiries {
  id bigint [pk, increment]

  student_id bigint [not null]
  listing_id bigint [not null]

  message text [not null]
  status enquiry_status [not null, default: 'PENDING']
  landlord_response text

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz

  Indexes {
    (student_id, listing_id)
  }
}

Table rentals {
  id bigint [pk, increment]

  student_id bigint [not null]
  listing_id bigint [not null]
  enquiry_id bigint [unique]

  start_date date [not null]
  end_date date
  status rental_status [not null, default: 'ACTIVE']

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}

Table maintenance_requests {
  id bigint [pk, increment]

  rental_id bigint [not null]

  title varchar(150) [not null]
  description text [not null]
  priority maintenance_priority [not null, default: 'LOW']
  status maintenance_status [not null, default: 'OPEN']

  created_at timestamptz [not null, default: `now()`]
  updated_at timestamptz
}


Ref: users.id - student_profiles.user_id
Ref: users.id - landlord_profiles.user_id
Ref: landlord_profiles.id < listings.landlord_id
Ref: locations.id < listings.location_id
Ref: listings.id < listing_images.listing_id
Ref: student_profiles.id < saved_listings.student_id
Ref: listings.id < saved_listings.listing_id
Ref: student_profiles.id < enquiries.student_id
Ref: listings.id < enquiries.listing_id
Ref: student_profiles.id < rentals.student_id
Ref: listings.id < rentals.listing_id
Ref: enquiries.id - rentals.enquiry_id
Ref: rentals.id < maintenance_requests.rental_id