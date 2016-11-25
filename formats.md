# Definition of data structure formats.

## Current:

Account
* First Name
* Last Name
* Account Number
* History

Account Event
* Balance Change
* Description

### SQL
Accounts
FirstName | LastName | AccountNumber | History (JSON text)

## Optimal:

Account
* First Name
* Last Name
* Account Number (indentifier)

Account Event
* Type (transfer | deposit | withdrawal)
* Receiver (if applicable)
* Sender (if applicable)
* Balance Change
* // Description can be generated based on available info

### SQL
Accounts
FirstName | LastName | AccountNumber

Events
Type (int) | Receiver (accountnumber) | Sender (accountnumber) | BalanceChange (double)