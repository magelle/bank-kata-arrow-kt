# Bank Kata with kotlin and Arrow

## First step example Mapping

### Create an accounts
I Should be able to create an account.

### Make a deposit
I should be able to make deposit of money on my account.

### Make a withdrawal
I should be able to withdraw mnoey from my account.
- Can't withdraw if I don't have enough money on my account

### Get the Statement
I should be able to get all operations I did on my account.

## Final scenarii
- I create an account
- I make a deposit of 1000
- I make a deposit of 2000
- I withdraw 500
- the statement is :

| date | credit | debit | balance |
| ---- | ------ | ----- | ------- |
| 14/01/2012 | | 500.00 | 2500.00 |
| 13/01/2012 | 2000.00 | | 3000.00 |
| 10/01/2012 | 1000.00 | | 1000.00 |