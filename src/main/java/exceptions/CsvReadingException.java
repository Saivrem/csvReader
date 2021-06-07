package exceptions;

public abstract class CsvReadingException extends Exception {

    protected StringBuilder customMessage = new StringBuilder();

    protected CsvReadingException(int row) {
        customMessage.append(String.format("Exception caught processing row - %d", row));
    }

    public String getCustomMessage() {
        return customMessage.length() != 0 ? customMessage.toString() : this.getClass().getSimpleName();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getCustomMessage());
    }

}
