# API Plan

Base URL:

```http
http://localhost:8080/api 
```

## Authentication

```http
POST /api/auth/register/student
POST /api/auth/register/landlord
POST /api/auth/login
GET  /api/auth/me
```
## Public Listings
```http
GET /api/listings
GET /api/listings/{id}
```

## Landlord Listings
```http
POST   /api/landlord/listings
GET    /api/landlord/listings
GET    /api/landlord/listings/{id}
PUT    /api/landlord/listings/{id}
DELETE /api/landlord/listings/{id}
```

## Admin
```http
GET /api/admin/landlords/pending
PUT /api/admin/landlords/{id}/approve
PUT /api/admin/landlords/{id}/reject

GET /api/admin/listings/pending
PUT /api/admin/listings/{id}/approve
PUT /api/admin/listings/{id}/reject
PUT /api/admin/listings/{id}/deactivate
```

## Saved Listings
```http
POST   /api/student/saved-listings/{listingId}
DELETE /api/student/saved-listings/{listingId}
GET    /api/student/saved-listings
```

## Enquiries
```http
POST /api/student/enquiries
GET  /api/student/enquiries
GET  /api/student/enquiries/{id}
PUT  /api/student/enquiries/{id}/cancel

GET /api/landlord/enquiries
PUT /api/landlord/enquiries/{id}/approve
PUT /api/landlord/enquiries/{id}/reject
```
## Rentals
```http
POST /api/landlord/rentals
GET  /api/landlord/rentals
GET  /api/landlord/rentals/{id}
PUT  /api/landlord/rentals/{id}/end
PUT  /api/landlord/rentals/{id}/cancel

GET /api/student/rentals
GET /api/student/rentals/{id}
```

## Maintenance Requests
```http
POST /api/student/rentals/{rentalId}/maintenance-requests
GET  /api/student/maintenance-requests
GET  /api/student/maintenance-requests/{id}
GET  /api/student/rentals/{rentalId}/maintenance-requests

GET /api/landlord/maintenance-requests
GET /api/landlord/maintenance-requests/{id}
GET  /api/landlord/rentals/{rentalId}/maintenance-requests
PUT /api/landlord/maintenance-requests/{id}/status
```

## Dashboard
```http
GET /api/admin/dashboard/stats
```