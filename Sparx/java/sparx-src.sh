#!/bin/sh
rm sparx-src.zip
find com -name "*.java" -print | zip sparx-src.zip -@
