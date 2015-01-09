import os
from tornado.web import UIModule

__author__ = 'Yustos'

class ModEntry(UIModule):
    def render(self, modName):
        return self.render_string(os.path.join("templates", "modules", modName))
