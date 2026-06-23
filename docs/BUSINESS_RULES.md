# Business Rules

## Users

1. A user has one role: STUDENT, LANDLORD, or ADMIN.
2. A student user has one student profile.
3. A landlord user has one landlord profile.
4. Admin users do not need student or landlord profiles.

## Landlords

1. A landlord starts with verification status PENDING.
2. A landlord must be APPROVED before creating listings.
3. A rejected landlord may have a rejection reason.

## Listings

1. A listing belongs to one landlord.
2. A listing belongs to one location.
3. A new listing starts with status PENDING.
4. Public users can only see listings where status is APPROVED and available is true.
5. A listing can have multiple images.
6. A listing should have at most one primary image.

## Enquiries

1. A student can send an enquiry for an approved listing.
2. A landlord can approve or reject enquiries for their own listings only.
3. A student can cancel their own pending enquiry.
4. An enquiry may result in zero or one rental.

## Rentals

1. A rental belongs to one student.
2. A rental belongs to one listing.
3. A rental may or may not come from an enquiry.
4. If a rental has an enquiry, the enquiry must belong to the same student and listing.
5. A listing should not have more than one active rental at the same time.
6. When a rental is ACTIVE, the listing should be unavailable.
7. When a rental is ENDED or CANCELLED, the listing may become available again.

## Maintenance Requests

1. A maintenance request belongs to one rental.
2. A student can only create a maintenance request for their own active rental.
3. A landlord can only view maintenance requests connected to their own listings.
4. A new maintenance request starts with status OPEN.