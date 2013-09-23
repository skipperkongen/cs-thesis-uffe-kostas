#!/usr/bin/python
## Skrevet af Uffe Gram Christensen (uffe@diku.dk) 14-11-2008
import re
import os.path
import string
import sys
import formattering

# Formats a  semicolon separated-file to a LaTeX table.
# Assumes the table contains numbers and that these use comma as
# the decimal point. The commas are replaced with periods.
def csvToTable(inFile, outFile):
    if(inFile == outFile):
        print "Input file must be different from output file\n";
        return 0
    read = open(inFile)
    write = open(outFile, 'w')
    nextLine = string.rstrip(read.readline(),"\n\r")
    # Starting the tabular
    write.write("\\begin{tabular}{}\n \\hline \n")
    while nextLine:
        # Formatting from danish numbers to english
        text = nextLine.replace(",",".")
        # Formatting the item separator
        text = text.replace(";","&")
        # Ending the line
        text = text + "\\\\ \\hline \n"
        # Writing to output file
        write.write(text)
        nextLine = string.rstrip(read.readline(),"\n\r")
    write.write("\\end{tabular}\n")        
    read.close()
    write.close()

def emph(inFile):
    outFile = "test.tex"
    csvToTable(inFile, outFile)
    formattering.replace(outFile,"$\\textbf{&","$\\textbf{")
    formattering.replace(outFile,"&}$","}$")
