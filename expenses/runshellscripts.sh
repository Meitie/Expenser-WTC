expenseOutput=$( curl -s --request GET 'http://localhost:8123/printID/' --include )
expenseID=${expenseOutput:(-38)}
expenseFull=$( echo $expenseID | sed "s/\"//g" )
expenseToPost="$( curl -s --request GET 'http://localhost:8123/expenses/'${expenseFull}'' --include )"
echo $expenseToPost > fullExpense.txt
echo $expenseToPost > fullExpense0.txt

lastLine=$( sed '1q;d' fullExpense.txt )
harry=$( echo $lastLine | sed 's/^.*Content-Length/Content-Length/' )
echo $harry > fullExpense.txt
bob=$( echo $lastLine | sed 's/^.*{"id"/{"id"/' )
echo $bob > fullExpense.txt

expenseFullPost=$( sed '1q;d' fullExpense.txt )
echo $expenseFullPost
output2=$( curl -s --request POST 'http://localhost:8123/expenses/' -d ''${expenseFullPost}'' --include )
echo $output2 > fullExpense.txt