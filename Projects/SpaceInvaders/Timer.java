package SpaceInvaders;

public class Timer {

    // Timeout duration
    private Long timeout;

    // Time when timeout will expire
    private Long expireTime;

    // Constructor that does not set the timer, it can be set later
    // using the set method
    public Timer() {
        this.timeout = (long) 0;
    } // Timer

    // Constructor that sets the timer
    public Timer(Long timeout) {
        expireTime = System.currentTimeMillis() + timeout;
        this.timeout = timeout;
    } // Timer

    // Sets the timer
    public void set(Long timeout) {
        expireTime = System.currentTimeMillis() + timeout;
        this.timeout = timeout;
    } // set

    // Gets the timer settting
    public Long timeout() {
         return this.timeout;
    } // timeout

    // Returns true when the timer is expired
    public Boolean expired() {

        if (expireTime <= System.currentTimeMillis()) {
            return true;
        } // if

        else {
            return false;
        } // else
    } // expired

    // Resets the timer, same as set except uses timeout previously set
    public void reset() {
        expireTime = System.currentTimeMillis() + timeout;
    } // reset
} // Timer
