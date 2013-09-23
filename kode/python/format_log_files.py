#!/usr/bin/python
## Skrevet af Uffe Gram Christensen (uffe@diku.dk) 16-11-2008
import re
import os.path
import string
import sys



def logFormat(inFile):
    # Defining the regular expressions to match
    regExp = []
    regExp.append(re.compile("Deterministic capacity = "))
    regExp.append(re.compile("Deterministic solution = "))
    regExp.append(re.compile("Fat capacity = "))
    regExp.append(re.compile("Fat solution = "))

    detRobustness = re.compile("Deterministic solution \(robustness\): ")
    fatRobustness = re.compile("Fat solution \(robustness\): ")

    newStep = re.compile("Best solution: ")
    finalSolution = re.compile("It contained the following:")
    iteration = 0

    bestSolution = []

    # Opening file and reading the lines one at the time
    read = open(inFile)
    textLine = string.rstrip(read.readline(),"\n\r")
    values = []
    numRegExp = len(regExp)
    while textLine:
        # Checing for the intitial four numbers
        for i in range(0,numRegExp):
            if regExp[i].match(textLine):
                values.append(re.split(regExp[i], textLine)[1])
        # Finding the deterministic robustness interval
        if detRobustness.match(textLine):
            detRobInterval = re.split(detRobustness, textLine)[1]
            detRobInterval = detRobInterval.replace("[","")
            detRobInterval = detRobInterval.replace("]","")
            detRobInterval = re.split(",",detRobInterval)
         # Finding the fat robustness interval
        if fatRobustness.match(textLine):
            fatRobInterval = re.split(fatRobustness, textLine)[1]
            fatRobInterval = fatRobInterval.replace("[","")
            fatRobInterval = fatRobInterval.replace("]","")
            fatRobInterval = re.split(",",fatRobInterval)
        if newStep.match(textLine):
            tmpRes = re.split(newStep, textLine)
            tmpRes = re.split(",",tmpRes[1])
            objVal = re.split("\(",tmpRes[0])[0]
            robHigh = re.split("\(",tmpRes[1])[0]
            robLow = re.split("\(",tmpRes[2])[0]
            iteration += 1
            bestSolution.append((iteration,objVal,robLow,robHigh))
        if finalSolution.match(textLine):
            textLine = string.rstrip(read.readline(),"\n\r")
            solution = re.split(",",textLine)
        textLine = string.rstrip(read.readline(),"\n\r")
    #values.append(detRobInterval)
    # Returning the values derived from the log file
    # values is an array containing (in order):
    #   deterministic capacity
    #   deterministic solution
    #   fat capacity
    #   fat solution
    # solution is an array containing boolean values representing if an
    #   item is included in the last solution or not
    # detRobInterval is an array containing the lower and upper bound
    #   on the robustness of the deterministic solution
    # fatRobInterval is an array containing the lower and upper bound
    #   on the robustness of the fat solution
    # bestSolution is an array containing a tuple for each iteration of
    #   the genetic algorithm. The tuple contains the following elements:
    #   iteration number
    #   objective value of the best solution of the iteration
    #   lower bound of the robustness of the best solution
    #   upper bound of the robustness of the best solution

    return (values, solution, detRobInterval, fatRobInterval, bestSolution)

# 
def formatDir(logDir, outputFile):
    write = open(outputFile,"w")
    fileList = os.listdir(logDir)
    fileStart = re.compile("repeat")
    kpStart = re.compile("log_kp")
#    write.write("Items;Weight Deviation;Capacity Deviation;Seed?;Deterministic Solution;Fat Solution;Fat Percentage of deterministic;Best Solution (GA);Best percentage of deterministic;Best pct - Fat pct;Best pct/Fat pct-1;Best improvement of loss;Robustness lower;Robustness upper;Earliest occurence of best solution;First solution;First solution/Best solution\n")

    write.write("Deterministic Solution;Fat Solution;Fat Percentage of deterministic;Best Solution (GA);Best percentage of deterministic;Best pct - Fat pct;Best pct/Fat pct-1;Best improvement of loss;Robustness lower;Robustness upper;Earliest occurence of best solution;First solution;First solution/Best solution\n")
    for i in fileList:
        if(os.path.isfile(logDir+i) and fileStart.match(i)):
            fileInfo = re.split("_", i)
            if(kpStart.match(i)):
                items = int(fileInfo[2])
                weightSpread = int(fileInfo[4])
                capSpread = int(fileInfo[6])
                seed = fileInfo[8]
                write.write(str(items) + ";" + str(weightSpread) + ";" +str(capSpread) + ";" + str(seed) + ";")
            (values, solution, detRobInterval, fatRobInterval, bestSolution) = logFormat(logDir+i)
            (iteration, objective, lower, upper) = bestSolution[0]
            firstSol = float(objective)
            (iteration, objective, lower, upper) = bestSolution[99]

            # The three solution values (deterministic, fat and GA best)
            detSol = float(values[1])
            fatSol = float(values[3])
            bestSol = float(objective)
            firstSolPct = firstSol/bestSol
            # Number of iterations
            numIter = int(iteration)
            # Percentage of the deterministic solution achieved by fat and best
            fatPct = fatSol/detSol
            bestSolPct = bestSol/detSol
            # Improvement of best over fat (3 different methods)
            impPctPoint = bestSolPct - fatPct
            impPct = (bestSol/fatSol)-1
            impPctOfLoss = 1-(detSol - bestSol)/(detSol - fatSol)
            # Finding the iteration that created the best solution
            earliestOccurrence = firstHigh(bestSolution)
            write.write(str(detSol)+";")
            write.write(str(fatSol) + ";"+str(fatPct)+";")
            write.write(str(bestSol) + ";"+str(bestSolPct)+";")
            write.write(str(impPctPoint) + ";"+str(impPct) + ";" +str(impPctOfLoss)+";")
            write.write(str(lower)+";"+str(upper)+";")
            write.write(str(earliestOccurrence)+";"+str(firstSol)+";"+str(firstSolPct)+"\n")
    write.close()
            
def firstHigh(numberArray):
    (iteration, highNumber, low, high) = numberArray[len(numberArray)-1]
    for i in range(0,len(numberArray)):
        (curIt, curObj, curLow, curHigh) = numberArray[i]
        if(curObj == highNumber):
            return i
        
                   
            

logfile = "../log/log_stochastic_knapsack_autogen_100.txt"
logdir =  "../log/"
outFile = "out.csv"
#logFormat(logfile)
formatDir(logdir, outFile)
