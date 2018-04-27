package utils.kuaidi;

public abstract class ExpressCompany {

	abstract public String checkOrderStatus(String orderNumber);

    protected boolean orderNumberCheck(String orderNumber) {
        if (orderNumber.length() > 30) {
            System.out.println("Order Number is too long. Attack? OrderNumber: " + orderNumber);
            return false;
        }

        return true;
    }
}
