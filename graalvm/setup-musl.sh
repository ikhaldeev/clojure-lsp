#!/usr/bin/env bash

set -euo pipefail

if  [[ -z "${CLOJURE_LSP_STATIC:-}" ]]; then
    echo "CLOJURE_LSP_STATIC wasn't set, skipping musl installation."
    exit 0
fi

if [[ -z "${CLOJURE_LSP_MUSL:-}" ]]; then
    echo "CLOJURE_LSP_MUSL wasn't set, skipping musl installation."
    exit 0
fi

apt-get update -y && apt-get install musl-tools -y

ZLIB_VERSION="1.2.11"

curl -O -sL "https://zlib.net/zlib-${ZLIB_VERSION}.tar.gz"

echo "c3e5e9fdd5004dcb542feda5ee4f0ff0744628baf8ed2dd5d66f8ca1197cb1a1 zlib-${ZLIB_VERSION}.tar.gz" |
    sha256sum --check
tar xf "zlib-${ZLIB_VERSION}.tar.gz"

arch=${CLOJURE_LSP_ARCH:-"x86_64"}
echo "ARCH: $arch"

cd "zlib-${ZLIB_VERSION}"
CC=musl-gcc ./configure --static --prefix="/usr/local"
make CC=musl-gcc
make install
cd ..

# Install libz.a in the correct place so ldd can find it
install -Dm644 "/usr/local/lib/libz.a" "/usr/lib/$arch-linux-musl/libz.a"
