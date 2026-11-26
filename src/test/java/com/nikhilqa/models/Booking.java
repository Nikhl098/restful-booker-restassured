package com.nikhilqa.models;

/** POJO for the Restful Booker booking payload. */
public class Booking {

    public String firstname;
    public String lastname;
    public int totalprice;
    public boolean depositpaid;
    public BookingDates bookingdates;
    public String additionalneeds;

    public Booking(String firstname, String lastname, int totalprice,
                   boolean depositpaid, BookingDates bookingdates, String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;
    }

    public static class BookingDates {
        public String checkin;
        public String checkout;

        public BookingDates(String checkin, String checkout) {
            this.checkin = checkin;
            this.checkout = checkout;
        }
    }
}
