#!/usr/bin/python
## Skrevet af Uffe Gram Christensen (uffe@diku.dk) 13-11-2008
import re
import os.path
import string
import sys

# Fixes a collection of scenarios at the same location
def fixScenarios(scenarios, scenario_base):
    for scenario in scenarios:
        fixScenario(scenario, scenario_base)

# Fixes a single scenario by inserting the required
# schema and schema location information        
def fixScenario(scenario, scenario_base):
    read = open(scenario_base+scenario+".xml")
    scenario_text = read.read()
    read.close()
    write = open(scenario_base+scenario+".xml","w")
    find = "<knapsack xmlns=\"http://www.diku.dk/robust/autogen/stochknapsack\">"
    replace_with = "<knapsack xmlns=\"http://www.diku.dk/robust/autogen/stochknapsack\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.diku.dk/robust/autogen/stochknapsack ../../schema/knapsack/knapsack_instance.xsd \">"
    text = scenario_text.replace(find,replace_with)
    write.write(text)
    write.close()
