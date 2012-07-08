# -*- coding: utf-8 -*-
"""

    Copyright [2012] [Redacted Labs]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
"""

from sys import argv
from handlers import start_server
from libs import ConsoleColors
from models import __create__

def serve():
    """
    serves the application
    ----------------------
    """
    start_server()

def create():
    """ Creates the database """
    print(ConsoleColors.INFO+'Creating the database ... ')
    __create__()
    print(ConsoleColors.INFO+'Completed database creation')

options = {'serve': serve, 'create': create}

if argv[1] in options.keys():
    options[argv[1]]()
else:
    print(ConsoleColors.warn+'Option does not exist')
