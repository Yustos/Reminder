from tornado.ioloop import IOLoop
from tornado.web import RequestHandler, Application, url, UIModule
from tornado import log
import logging
import Storage
import os
from api.ListServiceHandler import ListServiceHandler
from api.DetailServiceHandler import DetailServiceHandler
from api.SyncServiceHandler import SyncServiceHandler
from api.LogServiceHandler import LogServiceHandler
from optparse import OptionParser
from Logger import Logger
import uimodules

logInstance = None


class MainHandler(RequestHandler):
    def get(self, path):
        modName = "main.html"
        if path == "log":
            modName = "log.html"
        self.render(os.path.join("templates", "template.html"), title="Main", modName=modName)


class LoggerHandler(logging.Handler):
    def emit(self, record):
        logInstance.add("", self.format(record), record.logSource if hasattr(record, "logSource") else None)

    @staticmethod
    def LogRequest(handler):
            logInstance.add(handler.request.remote_ip, handler.request.uri)

settings = {
    "static_path": os.path.join(os.path.dirname(__file__), "static"),
    "ui_modules": uimodules,
    "log_function": LoggerHandler.LogRequest
}

application = Application([
    url(r"/main/(.*)", MainHandler, name="log"),
    url(r"/api/list(.*)", ListServiceHandler, name="listService"),
    url(r"/api/detail(.*)", DetailServiceHandler, name="detailService"),
    url(r"/api/sync(.*)", SyncServiceHandler, name="syncService"),
    url(r"/api/log(.*)", LogServiceHandler, name="logService"),
], **settings)

if __name__ == "__main__":
    opt = OptionParser()
    opt.add_option("-d", "--data", dest="data", help="Folder to store data")
    opts, args = opt.parse_args()

    Storage.Storage.path = opts.data
    Logger.path = opts.data
    logInstance = Logger()
    application.listen(9359)

    logInstance.add("local", "Start")
    log.app_log.addHandler(LoggerHandler())

    log.enable_pretty_logging()

    IOLoop.instance().start()

