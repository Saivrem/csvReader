package exceptions;

public abstract class CsvReadingException extends Exception {

    protected StringBuilder customMessage = new StringBuilder();

    protected CsvReadingException(int row) {
        customMessage.append(this.getClass().getSimpleName()).append("\n");
        customMessage.append(String.format("Exception caught processing row - %d\n", row));
    }

    public String getCustomMessage() {
        return customMessage.toString();
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
