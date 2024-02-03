KLEENEE_STAR = "*"
KLEENEE_PLUS = "+"
CONCAT = ""
UNION = "|"
PARENTHESIS_OPEN = "("
PARENTHESIS_CLOSE = ")"

#Each Node of Tree
class TreeNode:
    value = None
    left = None
    right = None

    def __init__(self, value):
        self.value = value
    def setLeft(self, left) :
        self.left = left
    def setRight(self, right) :
        self.right = right
    

re = input("Enter RE: ")
nodes = []
