CREATE TYPE user_role AS ENUM (
    'STUDENT',
    'LANDLORD',
    'ADMIN'
);

CREATE TYPE landlord_verification_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
);

CREATE TYPE gender_preference AS ENUM (
    'ANY',
    'MALE',
    'FEMALE'
);

CREATE TYPE listing_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED',
    'DEACTIVATED'
);

CREATE TYPE enquiry_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED',
    'CANCELLED'
);

CREATE TYPE maintenance_priority AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'URGENT'
);

CREATE TYPE maintenance_status AS ENUM (
    'OPEN',
    'IN_PROGRESS',
    'RESOLVED',
    'CLOSED'
);

CREATE TYPE rental_status AS ENUM (
    'ACTIVE',
    'ENDED',
    'CANCELLED'
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       role user_role NOT NULL DEFAULT 'STUDENT',
                       first_name VARCHAR(40) NOT NULL,
                       middle_name VARCHAR(40),
                       last_name VARCHAR(40) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(20) UNIQUE,
                       active BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ
);

CREATE TABLE student_profiles (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL UNIQUE,
                                  student_number VARCHAR(20) NOT NULL UNIQUE,
                                  institution_name VARCHAR(70) NOT NULL,
                                  preferred_area VARCHAR(100),
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                  updated_at TIMESTAMPTZ,

                                  CONSTRAINT fk_student_profiles_user
                                      FOREIGN KEY (user_id)
                                          REFERENCES users(id)
                                          ON DELETE CASCADE
);

CREATE TABLE landlord_profiles (
                                   id BIGSERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL UNIQUE,
                                   verification_status landlord_verification_status NOT NULL DEFAULT 'PENDING',
                                   business_name VARCHAR(100),
                                   id_document VARCHAR(255) NOT NULL,
                                   rejection_reason TEXT,
                                   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                   updated_at TIMESTAMPTZ,

                                   CONSTRAINT fk_landlord_profiles_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES users(id)
                                           ON DELETE CASCADE
);

CREATE TABLE locations (
                           id BIGSERIAL PRIMARY KEY,
                           area VARCHAR(100) NOT NULL,
                           city VARCHAR(100) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMPTZ
);

CREATE TABLE listings (
                          id BIGSERIAL PRIMARY KEY,
                          landlord_id BIGINT NOT NULL,
                          location_id BIGINT NOT NULL,
                          title VARCHAR(150) NOT NULL,
                          description TEXT NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          available BOOLEAN NOT NULL DEFAULT TRUE,
                          gender_preference gender_preference NOT NULL DEFAULT 'ANY',
                          furnished BOOLEAN NOT NULL DEFAULT FALSE,
                          rules TEXT,
                          status listing_status NOT NULL DEFAULT 'PENDING',
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ,

                          CONSTRAINT fk_listings_landlord
                              FOREIGN KEY (landlord_id)
                                  REFERENCES landlord_profiles(id),

                          CONSTRAINT fk_listings_location
                              FOREIGN KEY (location_id)
                                  REFERENCES locations(id),

                          CONSTRAINT chk_listings_price_positive
                              CHECK (price >= 0)
);

CREATE TABLE listing_images (
                                id BIGSERIAL PRIMARY KEY,
                                listing_id BIGINT NOT NULL,
                                image_url VARCHAR(500) NOT NULL,
                                is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                CONSTRAINT fk_listing_images_listing
                                    FOREIGN KEY (listing_id)
                                        REFERENCES listings(id)
                                        ON DELETE CASCADE
);

CREATE TABLE saved_listings (
                                id BIGSERIAL PRIMARY KEY,
                                student_id BIGINT NOT NULL,
                                listing_id BIGINT NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                CONSTRAINT fk_saved_listings_student
                                    FOREIGN KEY (student_id)
                                        REFERENCES student_profiles(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_saved_listings_listing
                                    FOREIGN KEY (listing_id)
                                        REFERENCES listings(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT uq_saved_listings_student_listing
                                    UNIQUE (student_id, listing_id)
);

CREATE TABLE enquiries (
                           id BIGSERIAL PRIMARY KEY,
                           student_id BIGINT NOT NULL,
                           listing_id BIGINT NOT NULL,
                           message TEXT NOT NULL,
                           status enquiry_status NOT NULL DEFAULT 'PENDING',
                           landlord_response TEXT,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMPTZ,

                           CONSTRAINT fk_enquiries_student
                               FOREIGN KEY (student_id)
                                   REFERENCES student_profiles(id),

                           CONSTRAINT fk_enquiries_listing
                               FOREIGN KEY (listing_id)
                                   REFERENCES listings(id)
);

CREATE TABLE rentals (
                         id BIGSERIAL PRIMARY KEY,
                         student_id BIGINT NOT NULL,
                         listing_id BIGINT NOT NULL,
                         enquiry_id BIGINT UNIQUE,
                         start_date DATE NOT NULL,
                         end_date DATE,
                         status rental_status NOT NULL DEFAULT 'ACTIVE',
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMPTZ,

                         CONSTRAINT fk_rentals_student
                             FOREIGN KEY (student_id)
                                 REFERENCES student_profiles(id),

                         CONSTRAINT fk_rentals_listing
                             FOREIGN KEY (listing_id)
                                 REFERENCES listings(id),

                         CONSTRAINT fk_rentals_enquiry
                             FOREIGN KEY (enquiry_id)
                                 REFERENCES enquiries(id),

                         CONSTRAINT chk_rentals_dates
                             CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE maintenance_requests (
                                      id BIGSERIAL PRIMARY KEY,
                                      rental_id BIGINT NOT NULL,
                                      title VARCHAR(150) NOT NULL,
                                      description TEXT NOT NULL,
                                      priority maintenance_priority NOT NULL DEFAULT 'LOW',
                                      status maintenance_status NOT NULL DEFAULT 'OPEN',
                                      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                      updated_at TIMESTAMPTZ,

                                      CONSTRAINT fk_maintenance_requests_rental
                                          FOREIGN KEY (rental_id)
                                              REFERENCES rentals(id)
                                              ON DELETE CASCADE
);

CREATE INDEX idx_student_profiles_user_id ON student_profiles(user_id);
CREATE INDEX idx_landlord_profiles_user_id ON landlord_profiles(user_id);

CREATE INDEX idx_listings_landlord_id ON listings(landlord_id);
CREATE INDEX idx_listings_location_id ON listings(location_id);
CREATE INDEX idx_listings_status_available ON listings(status, available);

CREATE INDEX idx_listing_images_listing_id ON listing_images(listing_id);

CREATE INDEX idx_saved_listings_student_id ON saved_listings(student_id);
CREATE INDEX idx_saved_listings_listing_id ON saved_listings(listing_id);

CREATE INDEX idx_enquiries_student_id ON enquiries(student_id);
CREATE INDEX idx_enquiries_listing_id ON enquiries(listing_id);
CREATE INDEX idx_enquiries_status ON enquiries(status);

CREATE INDEX idx_rentals_student_id ON rentals(student_id);
CREATE INDEX idx_rentals_listing_id ON rentals(listing_id);
CREATE INDEX idx_rentals_status ON rentals(status);

CREATE INDEX idx_maintenance_requests_rental_id ON maintenance_requests(rental_id);
CREATE INDEX idx_maintenance_requests_status ON maintenance_requests(status);

CREATE UNIQUE INDEX uq_listing_images_one_primary_per_listing
    ON listing_images(listing_id)
    WHERE is_primary = TRUE;

CREATE UNIQUE INDEX uq_rentals_one_active_per_listing
    ON rentals(listing_id)
    WHERE status = 'ACTIVE';