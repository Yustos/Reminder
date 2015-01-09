from tornado.web import RequestHandler
from Logger import Logger

class LogServiceHandler(RequestHandler):
    def __init__(self, application, request, **kwargs):
        self._storage = Logger()
        return super(LogServiceHandler, self).__init__(application, request, **kwargs)

    def get(self, path):
        date = self.get_argument("date", default=None, strip=False)
        limit = self.get_argument("limit", default=None, strip=False)
        source = self.get_argument("source", default=None, strip=False)
        items = self._storage.get(float(date), int(limit), None if source is None else int(source))
        self.write({"items": items})
