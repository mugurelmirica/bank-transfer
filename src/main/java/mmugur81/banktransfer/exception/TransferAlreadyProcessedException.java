package mmugur81.banktransfer.exception;

public class TransferAlreadyProcessedException extends TransferException {

    public TransferAlreadyProcessedException(long transferId) {
        super("Transfer " + transferId + " already processed");
    }
}
