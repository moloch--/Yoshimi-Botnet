# -*- coding: utf-8 -*-
'''
Created on Mar 12, 2012

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

from os import urandom
from base64 import b64encode
from hashlib import sha256
from sqlalchemy import Column, ForeignKey
from sqlalchemy.orm import synonym, relationship, backref
from sqlalchemy.types import Unicode, Integer, Boolean
from models import dbsession
from models.Permission import Permission
from models.BaseObject import BaseObject

def gen_salt():
    return unicode(b64encode(urandom(24)))

class User(BaseObject):
    """ User definition """

    user_name = Column(Unicode(64), unique = True, nullable = False)
    permissions = relationship("Permission", backref = backref("User", lazy = "joined"), cascade = "all, delete-orphan")
    salt = Column(Unicode(32), unique = True, nullable = False, default = gen_salt)
    _password = Column('password', Unicode(128))
    password = synonym('_password', descriptor = property(
            lambda self: self._password,
            lambda self, password: setattr(self, '_password', self.__class__._hash_password(password, self.salt))
        )
    )
        
    @classmethod
    def by_id(cls, user_id):
        """ Return the user object whose user id is ``user_id`` """
        return dbsession.query(cls).filter_by(id = user_id).first()
    
    @classmethod
    def get_all(cls):
        """ Return all non-admin user objects """
        return dbsession.query(cls).filter(cls.user_name != 'admin').all() 

    @classmethod
    def by_user_name(cls, user_name):
        """ Return the user object whose user name is ``user_name`` """
        return dbsession.query(cls).filter_by(user_name = unicode(user_name)).first()
    
    @classmethod
    def _hash_password(cls, password, salt):
        ''' Hashes the password using 25,000 rounds of salted SHA-256, come at me bro '''
        sha = sha256()
        sha.update(password + salt)
        for count in range(0, 25000):
            sha.update(sha.hexdigest())
        return sha.hexdigest()
    
    @property
    def queued_jobs(self):
        return filter(lambda job: (job.completed == False), self.jobs)
    
    @property
    def completed_jobs(self):
        return filter(lambda job: (job.completed == True), self.jobs)

    @property
    def permissions(self):
        """ Return a list with all permissions granted to the user """
        return dbsession.query(Permission).filter_by(user_id = self.id)

    @property
    def permissions_names(self):
        """ Return a list with all permissions names granted to the user """
        return [permission.permission_name for permission in self.permissions]
    
    def has_permission(self, permission):
        """ Return True if 'permission' is in permissions_names """
        return True if permission in self.permissions_names else False

    def validate_password(self, attempt):
        """ Check the password against existing credentials """
        if isinstance(attempt, unicode):
            attempt = attempt.encode('utf-8', 'ignore')
        return self.password == self._hash_password(attempt, self.salt)
    
    def __repr__(self):
        return ('<User - user_name: %s>' % (self.user_name,)).encode('utf-8', 'ignore')
