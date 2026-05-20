## Overview

This repository builds a custom, minimal Linux image for a Raspberry Pi and is a companion to the [plfluidics microfluidic control application](https://github.com/robertpuccinelli/plfluidics). The custom image installs all of the required software and can either be accessed remotely with Tailscale or directly in a kiosk-mode interface.

## Build Instructions

To build the image, install the tool called [kas](https://github.com/siemens/kas) to automate image compilation from the configuration file included in this repository. All dependencies will automatically be pulled. Using a 5 cores of a Ryzen 3700X CPU and 32 GB of RAM, it took 4.5 hours to compile.

Once the image has been compiled, it can be found in the directory build/temp/deploy/raspberrypi/images/. Look for a large file that is ~250MB large and flash it onto an SD card.
