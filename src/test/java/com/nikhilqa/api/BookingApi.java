package com.nikhilqa.api;

import com.nikhilqa.models.Booking;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/** Thin client around the Restful Booker endpoints used by the tests. */
public class BookingApi {

    public static final String BASE_URI = "https://restful-booker.herokuapp.com";

    static {
        RestAssured.baseURI = BASE_URI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        // The API answers 418 when the Content-Type carries a charset suffix, so keep it plain.
        RestAssured.config = RestAssured.config().encoderConfig(
                io.restassured.config.EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    private static RequestSpecification request() {
        return given().contentType("application/json").accept("application/json");
    }

    public static Response ping() {
        return given().when().get("/ping");
    }

    public static Response createToken(String username, String password) {
        return request()
                .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
                .when().post("/auth");
    }

    public static Response createBooking(Booking booking) {
        return request().body(booking).when().post("/booking");
    }

    public static Response getBooking(int id) {
        return request().when().get("/booking/" + id);
    }

    public static Response updateBooking(int id, Booking booking, String token) {
        return request().header("Cookie", "token=" + token)
                .body(booking).when().put("/booking/" + id);
    }

    public static Response partialUpdateBooking(int id, String jsonPatch, String token) {
        return request().header("Cookie", "token=" + token)
                .body(jsonPatch).when().patch("/booking/" + id);
    }

    public static Response updateBookingWithoutToken(int id, Booking booking) {
        return request().body(booking).when().put("/booking/" + id);
    }

    public static Response deleteBooking(int id, String token) {
        return given().header("Cookie", "token=" + token)
                .when().delete("/booking/" + id);
    }
}
