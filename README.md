# Regular Expression to Definitive Finite Automata

## A project using Java SE 17 for processing RE to node transition csv file, and using Python for building csv file into Formal Description Excel

### Setup

1. Download python 3 and these packages using pip: pandas, openpyxl.

    ```bash
        pip install pandas openpyxl
    ```

    - Optional: Download pyarrow for more pandas supports.

    ```bash
        pip install pyarrow
    ```

2. Download Java SE 17 from Oracle. [https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html]

### To Run

1. Run main function on /src/main/java/RegexToDfa.java to get output.csv.

2. Run Python to get result.xlsx based on output.csv.

    ```bash
        python3 createExcel.py
    ```

### Explaination of output.csv

First Column: Taken Node.

Second Column: An alphabet required on transitioning.

Third Column: Transitioned Node.

Fourth Column: Accept State of Taken Node.