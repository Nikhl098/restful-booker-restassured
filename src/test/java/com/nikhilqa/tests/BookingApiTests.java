package com.nikhilqa.tests;

import com.nikhilqa.api.BookingApi;
import com.nikhilqa.models.Booking;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * API automation for restful-booker.herokuapp.com with REST Assured.
 * Each test creates its own booking, so the tests are independent and
 * safe to run in any order.
 */
public class BookingApiTests {

    private String token;

    private static Booking sampleBooking() {
        return new Booking("Nikhil", "Singh", 1200, true,
                new Booking.BookingDates("2026-12-01", "2026-12-05"), "Late checkout");
    }

    private static int createBookingAndGetId() {
        Response res = BookingApi.createBooking(sampleBooking());
        Assert.assertEquals(res.getStatusCode(), 200, "Create booking failed: " + res.asString());
        return res.jsonPath().getInt("bookingid");
    }

    @BeforeClass
    public void obtainToken() {
        Response res = BookingApi.createToken("admin", "password123");
        Assert.assertEquals(res.getStatusCode(), 200, "Auth request failed");
        token = res.jsonPath().getString("token");
        Assert.assertNotNull(token, "No token in auth response: " + res.asString());
    }

    @Test(description = "TC-B01: GET /ping answers 201 Created")
    public void ping_returnsCreated() {
        Assert.assertEquals(BookingApi.ping().getStatusCode(), 201, "Ping should return 201");
    }

    @Test(description = "TC-B02: POST /booking stores the booking and returns an id")
    public void createBooking_returnsIdAndData() {
        Response res = BookingApi.createBooking(sampleBooking());
        Assert.assertEquals(res.getStatusCode(), 200, res.asString());
        Assert.assertTrue(res.jsonPath().getInt("bookingid") > 0, "bookingid missing");
        Assert.assertEquals(res.jsonPath().getString("booking.firstname"), "Nikhil");
        Assert.assertEquals(res.jsonPath().getInt("booking.totalprice"), 1200);
        Assert.assertEquals(res.jsonPath().getString("booking.bookingdates.checkin"), "2026-12-01");
    }

    @Test(description = "TC-B03: GET /booking/{id} returns the same data that was posted")
    public void getBooking_returnsSameData() {
        int id = createBookingAndGetId();
        Response res = BookingApi.getBooking(id);
        Assert.assertEquals(res.getStatusCode(), 200, res.asString());
        Assert.assertEquals(res.jsonPath().getString("firstname"), "Nikhil");
        Assert.assertEquals(res.jsonPath().getString("lastname"), "Singh");
        Assert.assertEquals(res.jsonPath().getString("additionalneeds"), "Late checkout");
    }

    @Test(description = "TC-B04: PUT /booking/{id} with a token replaces the booking")
    public void updateBooking_withToken_replacesData() {
        int id = createBookingAndGetId();
        Booking updated = new Booking("Updated", "Name", 2500, false,
                new Booking.BookingDates("2026-12-10", "2026-12-12"), "Breakfast");
        Response res = BookingApi.updateBooking(id, updated, token);
        Assert.assertEquals(res.getStatusCode(), 200, res.asString());

        Response check = BookingApi.getBooking(id);
        Assert.assertEquals(check.jsonPath().getString("firstname"), "Updated");
        Assert.assertEquals(check.jsonPath().getInt("totalprice"), 2500);
        Assert.assertFalse(check.jsonPath().getBoolean("depositpaid"), "depositpaid should be false");
    }

    @Test(description = "TC-B05: PATCH /booking/{id} changes only the given fields")
    public void partialUpdateBooking_changesOnlyGivenFields() {
        int id = createBookingAndGetId();
        Response res = BookingApi.partialUpdateBooking(id,
                "{\"firstname\":\"Patched\",\"totalprice\":999}", token);
        Assert.assertEquals(res.getStatusCode(), 200, res.asString());

        Response check = BookingApi.getBooking(id);
        Assert.assertEquals(check.jsonPath().getString("firstname"), "Patched");
        Assert.assertEquals(check.jsonPath().getInt("totalprice"), 999);
        Assert.assertEquals(check.jsonPath().getString("lastname"), "Singh",
                "Untouched field lastname should stay unchanged");
    }

    @Test(description = "TC-B06: PUT without a token is rejected with 403")
    public void updateBooking_withoutToken_isForbidden() {
        int id = createBookingAndGetId();
        Response res = BookingApi.updateBookingWithoutToken(id, sampleBooking());
        Assert.assertEquals(res.getStatusCode(), 403,
                "Expected 403 without token, got " + res.getStatusCode());
    }

    @Test(description = "TC-B07: DELETE /booking/{id} removes it, a later GET returns 404")
    public void deleteBooking_thenGet_returnsNotFound() {
        int id = createBookingAndGetId();
        Response del = BookingApi.deleteBooking(id, token);
        Assert.assertEquals(del.getStatusCode(), 201, "Delete should return 201");
        Assert.assertEquals(BookingApi.getBooking(id).getStatusCode(), 404,
                "Deleted booking should return 404");
    }
}
