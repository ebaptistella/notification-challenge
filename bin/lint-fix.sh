#!/bin/bash
# Script to execute all lint correction commands
# Usage: ./bin/lint-fix.sh or lein run-script lint-fix

set -e

echo "🔧 Executing automatic lint corrections..."
echo ""

echo "📝 1/4 - Organizing namespaces (clojure-lsp clean-ns)..."
lein clojure-lsp clean-ns || true

echo "✨ 2/4 - Formatting code (clojure-lsp format)..."
lein clojure-lsp format || true

echo "🎨 3/4 - Formatting with cljfmt..."
lein cljfmt fix || true

echo "📦 4/4 - Organizing requires (nsorg)..."
lein nsorg --replace || true

echo ""
echo "✅ All corrections have been applied!"
echo "💡 Execute 'lein lint' to check if there are still problems."
