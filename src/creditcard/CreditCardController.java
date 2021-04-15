package creditcard;

import banking.NotifyAssessor;
import banking.NotifyProperty;
import banking.models.BankingAccountFactory;
import creditcard.models.CreditCardAccountFactory;
import framework.controllers.CommandManager;
import framework.controllers.Controller;
import framework.controllers.commands.ENotify;
import framework.controllers.commands.LoggableAction;
import framework.controllers.commands.Proxy;
import framework.controllers.commands.Withdraw;
import framework.controllers.results.IResult;
import framework.controllers.ruleengine.AbstractAssessor;
import framework.controllers.ruleengine.IProperty;
import framework.controllers.ruleengine.Rule;
import framework.models.account.Account;
import framework.models.account.Entry;
import framework.models.account.IAccount;
import framework.models.account.IEntry;
import framework.models.customer.Customer;
import framework.models.customer.CustomerFactory;
import framework.models.customer.ICustomer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class CreditCardController extends Controller {

    CreditCard creditCard;
    CommandManager commandManager;

    public CreditCardController(CreditCard creditCard) {
        this.creditCard = creditCard;
        this.commandManager = creditCard.getCommandManager();
    }

    @Override
    protected void deposit(IEntry entry, Account account) {
        // Todo
    }

    @Override
    protected void withdraw(IEntry entry, Account account) {
        LoggableAction withdraw = new Withdraw(entry, account);
        withdraw = new Proxy(withdraw, this.creditCard.getRepFile());
        IResult result = commandManager.submit(withdraw);
        System.out.println("Withdraw Report Added :\n" + this.creditCard.getRepFile().toString());
        this.creditCard.getAccFile().updateAccount(account);
    }

    @Override
    protected void addInterest() {
        // Todo
    }

    @Override
    protected void addAccount(String ctype) {
        Account account = CreditCardAccountFactory.createAccount(this.creditCard.accountType, this.creditCard.ccnumber);
        Customer customer = this.creditCard.getCusFile().get((Customer c) -> c.getEmail().equals(this.creditCard.email));
        if (customer == null) {
            customer = CustomerFactory.createCustomer(ctype);
        }
        // TODO :: refactor them with constructor to take all properties
        customer.setEmail(this.creditCard.email);
        customer.setCity(this.creditCard.city);
        customer.setName(this.creditCard.clientName);
        customer.addAccount((IAccount) account);
        account.setOwner(customer);
        // Todo: add expire date to account
        this.creditCard.getAccFile().addAccount(account);
    }

    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();
        if (object == this.creditCard.JButton_NewCCAccount)
            JButtonNewCCAC_actionPerformed(event);
        else if (object == this.creditCard.JButton_GenBill)
            JButtonGenerateBill_actionPerformed(event);
        else if (object == this.creditCard.JButton_Deposit)
            JButtonDeposit_actionPerformed(event);
        else if (object == this.creditCard.JButton_Withdraw)
            JButtonWithdraw_actionPerformed(event);
    }

    void JButtonNewCCAC_actionPerformed(ActionEvent event) {
        /*
         * JDialog_AddPAcc type object is for adding personal information construct a
         * JDialog_AddPAcc type object set the boundaries and show it
         */

        JDialog_AddCCAccount ccac = new JDialog_AddCCAccount(this.creditCard);
        ccac.setBounds(450, 20, 300, 380);
        ccac.show();
        // Todo :: add account logic
        addAccount(ICustomer.PERSON);

    }

    void JButtonGenerateBill_actionPerformed(ActionEvent event) {
        JDialogGenBill billFrm = new JDialogGenBill();
        billFrm.setBounds(450, 20, 400, 350);
        billFrm.show();

    }

    void JButtonDeposit_actionPerformed(ActionEvent event) {
        // get selected name
        int selection = this.creditCard.getView().getTableSelection();
        if (selection >= 0) {
            String name = (String) this.creditCard.getModel().getValueAt(selection, 0);

            // Show the dialog for adding deposit amount for the current mane
            JDialog_Deposit dep = new JDialog_Deposit(this.creditCard, name);
            dep.setBounds(430, 15, 275, 140);
            dep.show();

            // compute new amount
           // Todo :: Deposit logic
        }
    }

    void JButtonWithdraw_actionPerformed(ActionEvent event) {
        // get selected name
        int selection = this.creditCard.getView().getTableSelection();
        if (selection >= 0) {
            String cc_number = (String) this.creditCard.getModel().getValueAt(selection, 1);
            String name = (String) this.creditCard.getModel().getValueAt(selection, 0);

            // Show the dialog for adding withdraw amount for the current mane
            JDialog_Withdraw wd = new JDialog_Withdraw(this.creditCard, name);
            wd.setBounds(430, 15, 275, 140);
            wd.show();

            //  Todo :: Withdraw logic and compute new amount
            IEntry entry = new Entry(this.creditCard.amountDeposit, LocalDate.now());
            Account account = this.creditCard.getAccFile().get((Account a) -> a.getId().equals(cc_number));
            withdraw(entry, account);
        }

    }
}
