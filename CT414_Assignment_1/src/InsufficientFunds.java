public  class InsufficientFunds extends Exception {
    public InsufficientFunds(String insufficient_funds){
        super(insufficient_funds);
    }
}
