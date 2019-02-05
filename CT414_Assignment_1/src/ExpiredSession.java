public class ExpiredSession extends Exception {

    public ExpiredSession(String session_Timed_Out) {
        super(session_Timed_Out);
    }
    
}
