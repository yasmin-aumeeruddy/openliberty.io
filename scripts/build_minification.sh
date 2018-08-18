# pushd tools/minification

# # JS Minification
# # Google Closure Compiler
# curl -O https://dl.google.com/closure-compiler/compiler-latest.zip
# unzip -q compiler-latest.zip -y
# cd compiler-latest
# mv closure-compiler-*.jar closure-compiler-latest.jar

# # HTML Minification
# popd

for js in $(find ./target/jekyll-webapp/assets -name *.js)
do
        java -jar ./tools/minification/closure-compiler-latest.jar --js $js --js_output_file $js.min
        mv $js.min $js
done