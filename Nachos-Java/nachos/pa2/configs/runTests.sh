#!/bin/bash

echo "Starting Tests"
java nachos.machine.Machine -[] pa1_sp.conf
java nachos.machine.Machine -[] pa1_dp.conf
java nachos.machine.Machine -[] pa1_ml.conf
java nachos.machine.Machine -[] pa1_sptest.conf
java nachos.machine.Machine -[] pa1_dptest.conf
java nachos.machine.Machine -[] pa1_mltest.conf
