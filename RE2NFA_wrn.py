KLEENEE_STAR = "*"
KLEENEE_PLUS = "+"
CONCAT = ""
UNION = "|"
PARENTHESIS_OPEN = "("
PARENTHESIS_CLOSE = ")"

regex = input("Enter Regular Expression: ")
# loop through the regex and check for the operators
for i in range(len(regex)):
    # check for parenthesis to determine the scope of the operators
        if regex[i] == PARENTHESIS_OPEN:
            # check for the closing parenthesis
            for j in range(i, len(regex)):
                if regex[j] == PARENTHESIS_CLOSE:
                    # handle the parenthesis

def parenthesis_handler():