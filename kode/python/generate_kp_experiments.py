#!/usr/bin/python
## Skrevet af Uffe Gram Christensen (uffe@diku.dk) 13-11-2008
import re
import os.path
import string
import sys


# Generates an experiment file for a scenario by
# reading a valid experiment file and altering the scenario
# and setting the seed value.
def generateExperiment(scenario, experiment_old, experiment_base, seed):
    # Opening and reading template experiment
    read = open(experiment_base+experiment_old)
    old = read.read()
    read.close()
    # Readying the file to write to
    write = open(experiment_base+scenario+"_"+seed+"_seed.xml","w")
    # Setting the strings to replace
    old_log = "log_stochastic_knapsack_autogen_100"
    old_scenario = "stochastic_knapsack_autogen_100_items"
    old_seed = "use_scenario_seeding=\"true\""
    new_log = "log_"+scenario+"_"+seed+"_seed"
    new_seed = "use_scenario_seeding=\""
    # Replacing the texts
    old = old.replace(old_log, new_log)
    old = old.replace(old_scenario, scenario)
    old = old.replace(old_seed, new_seed+seed+"\"")
    write.write(old)
    # Closing the file
    write.close()
 
scenario_base = "../xml/kp_instances/"
experiment_base = "../xml/kp_experiments/"
scenarios = ["kp_50_items_50_weightSpread_50_capSpread"
,"kp_50_items_50_weightSpread_10_capSpread"
,"kp_50_items_50_weightSpread_0_capSpread"
,"kp_50_items_10_weightSpread_50_capSpread"
,"kp_50_items_10_weightSpread_10_capSpread"
,"kp_50_items_10_weightSpread_0_capSpread"
,"kp_50_items_0_weightSpread_50_capSpread"
,"kp_50_items_0_weightSpread_10_capSpread"
,"kp_200_items_50_weightSpread_50_capSpread"
,"kp_200_items_50_weightSpread_10_capSpread"
,"kp_200_items_50_weightSpread_0_capSpread"
,"kp_200_items_10_weightSpread_50_capSpread"
,"kp_200_items_10_weightSpread_10_capSpread"
,"kp_200_items_10_weightSpread_0_capSpread"
,"kp_200_items_0_weightSpread_50_capSpread"
,"kp_200_items_0_weightSpread_10_capSpread"]
experiments = "kp_50_items_50_weightSpread_50_capSpread"
template_experiment = "knapsack_experiment_template.xml"

for scenario in scenarios:
    generateExperiment(scenario, template_experiment, experiment_base, "true")
    generateExperiment(scenario, template_experiment, experiment_base, "false")
