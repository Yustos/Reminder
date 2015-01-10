from tornado.web import RequestHandler
from Logger import LogApi
import Storage



class ListServiceHandler(RequestHandler):
    def __init__(self, application, request, **kwargs):
        self._storage = Storage.Storage()
        return super(ListServiceHandler, self).__init__(application, request, **kwargs)

    @LogApi()
    def get(self, path):
        showDeleted = self.get_argument("deleted", default=None, strip=False)
        records = self._storage.get(int(showDeleted))
        items = {"items": [{"id": i.id, "date": i.date,"title": i.title, "state": i.state} for i in records]}
        self.write(items)
