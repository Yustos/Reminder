# -*- coding: utf-8 -*-
import functools
from CodernityDB.database import Database
from CodernityDB.tree_index import TreeBasedIndex
from tornado import log
import time
import os

API_Source = 1


class LogApi(object):
    def __init__(self, logger=None):
        self.logger = logger

    def __call__(self, func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            self.log_api(True, func.__name__, args[0].request.uri, args[0].request.body)
            f_result = func(*args, **kwargs)
            self.log_api(False, func.__name__, args[0].request.uri, "\r\n".join(str(i) for i in args[0]._write_buffer))
            return f_result

        return wrapper

    def log_api(self, is_start, name, uri, body):
        log.app_log.info("[%s]\r\n%s\r\n%s\r\n%s" % ('begin' if is_start else 'end', name, uri, body),
                         extra={"logSource": API_Source})


class Logger():
    path = None

    def __init__(self):
        if Logger.path is None:
            raise Exception("Path for data not set")

        db = Database(os.path.join(Logger.path, "log"))
        if db.exists():
            db.open()
        else:
            db.create()
            index = LogIndex(db.path, 'logIndex')
            db.add_index(index)

        self._db = db

    def get(self, date, limit, source):
        if source is None:
            records = self._db.get_many("logIndex", limit=limit, end=date, inclusive_end=False, with_doc=True)
        else:
            records = self._get(date, limit, source)
        return [dict(
            date=r["doc"]["date"],
            client=r["doc"]["client"],
            message=r["doc"]["message"],
            source=r["doc"].get("source")) for r in records]

    def _get(self, date, limit, source):
        items = self._db.get_many("logIndex", limit=-1, end=date, inclusive_end=False, with_doc=True)
        for r in items:
            if r["doc"].get("source") == source:
                limit -= 1
                yield r
            if not limit:
                break

    def add(self, client, message, source=None):
        item = dict(date=time.time(), client=client, message=message)
        if source is not None:
            item["source"] = source
        self._db.insert(item)


class LogIndex(TreeBasedIndex):
    def __init__(self, *args, **kwargs):
        kwargs['node_capacity'] = 7
        kwargs['key_format'] = 'd'
        super(LogIndex, self).__init__(*args, **kwargs)

    def make_key(self, key):
        return key

    def make_key_value(self, data):
        a_val = data.get('date')
        if a_val is not None:
            return a_val, None
