#!/usr/bin/python
## Skrevet af Uffe Gram Christensen (uffe@diku.dk) 03-11-2008
import re
import os.path
import string
import sys

# query replaces a string in a number of similarily named
# files with another string.
def replace(inFile, find, replace_with):
    read = open(inFile)
    text = read.read()
    read.close()
    write = open(inFile, "w")
    text = text.replace(find,replace_with)
    write.write(text)
    write.close()

# Finds a string in all files in a directory and replaces it with a selected string
def replaceDir(directory, find, replace_with):
    fileList = os.listdir(directory)
    for i in fileList:
        if(os.path.isfile(i)):
            replace(directory+i, find, replace_with)
