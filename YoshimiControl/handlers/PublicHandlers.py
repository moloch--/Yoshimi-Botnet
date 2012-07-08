# -*- coding: utf-8 -*-
'''
Created on Mar 13, 2012

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


import os
import logging

from tornado.web import RequestHandler
from string import ascii_letters, digits
from libs.Session import SessionManager
from models import User

class WelcomeHandler(RequestHandler):

    def get(self, *args, **kwargs):
        ''' Renders the welcome page '''
        self.render("public/welcome.html")

class LoginHandler(RequestHandler):

    def initialize(self, dbsession):
        self.dbsession = dbsession

    def get(self, *args, **kwargs):
        ''' Renders the login page '''
        self.render("public/login.html", message = "User Authentication")

    def post(self, *args, **kwargs):
        ''' Checks login creds '''
        try:
            user_name = self.get_argument('username')
            user = User.by_user_name(user_name.encode('utf-8', 'ignore'))
        except:
            self.render('public/login.html', message = "Type in an account name")
            return
        try:
            password = self.get_argument('password')
        except:
            self.render('public/login.html', message = "Type in a password")
            return
        if user != None and user.validate_password(password):
            logging.info("Successful login: %s from %s" % (user.user_name, self.request.remote_ip))
            session_manager = SessionManager.Instance()
            sid, session = session_manager.start_session()
            self.set_secure_cookie(name = 'auth', value = str(sid), expires_days = 1, HttpOnly = True)
            session.data['user_name'] = str(user.user_name)
            session.data['ip'] = str(self.request.remote_ip)
            if user.has_permission('admin'):
                session.data['menu'] = str('admin')
            else:
                session.data['menu'] = str('user')
            self.redirect('/home')
        else:
            logging.info("Failed login attempt from %s " % self.request.remote_ip)
            self.render('public/login.html', message = "Failed login attempt, try again")

class RegistrationHandler(RequestHandler):
    
    def initialize(self, dbsession):
        self.dbsession = dbsession

    def get(self, *args, **kwargs):
        ''' Renders registration page '''
        self.render("public/registration.html", message = "Fill out the form below")

    def post(self, *args, **kwargs):
        ''' Attempts to create an account, with shitty form validation '''
        # Check user_name parameter
        try:
            user_name = self.get_argument('username')
        except:
            self.render('public/registration.html', message = 'Please enter a valid account name')
        # Check password parameter
        try:
            password1 = self.get_argument('pass1')
            password2 = self.get_argument('pass2')
            if password1 != password2:
                self.render('public/registration.html', message = 'Passwords did not match')
            else:
                password = password1
        except:
            self.render('public/registration.html', message = 'Please enter a password')
        # Strip any non-white listed chars
        char_white_list = ascii_letters + digits
        user_name = filter(lambda char: char in char_white_list, user_name)
        # Check parameters
        if User.by_user_name(user_name) != None:
            self.render('public/registration.html', message = 'Account name already taken')
        elif len(user_name) < 3 or 15 < len(user_name):
            self.render('public/registration.html', message = 'Username must be 3-15 characters')
        elif len(password) < 12:
            self.render('public/registration.html', message = 'Password must be 12+ characters')
        else:
            user = User(
                user_name = unicode(user_name),
            )
            # Create user, init class variables
            self.dbsession.add(user)
            self.dbsession.flush()
            # Set password for user
            user.password = password.encode('utf-8', 'ignore')
            self.dbsession.add(user)
            self.dbsession.flush()
            self.render("public/account_created.html", user = user)

class AboutHandler(RequestHandler):
    
    def get(self, *args, **kwargs):
        ''' Renders the about page '''
        self.render("public/about.html")