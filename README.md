# Restful Booker API automation (Java, REST Assured, TestNG)

API test automation for [restful-booker.herokuapp.com](https://restful-booker.herokuapp.com/apidoc/index.html). It automates the Postman checks from my [Restful Booker API testing](https://nikhl098.github.io/restful-booker.html) project in code.

**Stack:** Java 11, REST Assured 5.5, TestNG 7.10, Jackson, Maven.

## Latest run: 7 passed, 0 failed (26 Nov 2026)

| ID | Test | Result |
|----|------|--------|
| TC-B01 | GET /ping returns 201 | Pass |
| TC-B02 | POST /booking returns id and echoes data | Pass |
| TC-B03 | GET /booking/{id} returns the posted data | Pass |
| TC-B04 | PUT with token replaces the booking | Pass |
| TC-B05 | PATCH changes only the given fields | Pass |
| TC-B06 | PUT without token returns 403 | Pass |
| TC-B07 | DELETE then GET returns 404 | Pass |

## Structure
- `models/Booking`: request payload POJO.
- `api/BookingApi`: small client, one method per endpoint.
- `tests/BookingApiTests`: independent tests, each creates its own booking.

## Run
```
mvn test
```

## Notes from building it
- The API answered `418 I'm a Teapot` to requests with REST Assured's default `Accept` list and charset-suffixed `Content-Type`. Sending plain `application/json` for both fixed it (see `BookingApi`).
- This is a shared public demo API, so bookings from other users can appear; tests only use the ids they create.

## Test data
Only the public demo credentials published by the demo sites themselves are used (SauceDemo's login page lists its test users; Restful Booker's API docs list the admin demo login). No private credentials are in this repo.

## Credits
Built with AI assistance (Instinct); tests were run and debugged against the live API, and the results above come from that run.
