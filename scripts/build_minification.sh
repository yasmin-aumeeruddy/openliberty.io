mkdir tools
mkdir tools/minification

pushd tools/minification
# JS Minification
# Google Closure Compiler
curl -O https://dl.google.com/closure-compiler/compiler-latest.zip
unzip -q compiler-latest.zip
mv closure-compiler-*.jar closure-compiler-latest.jar
popd
for js in $(find ./target/jekyll-webapp/assets -name *.js)
do
        java -jar ./tools/minification/closure-compiler-latest.jar --js $js --js_output_file $js.min
        mv $js.min $js
done

# CSS minification
# CSSO Gem
gem install csso-rails
for css in $(find ./target/jekyll-webapp/assets -name *.css)
do
        ruby_csso $css > $css.min
        mv $css.min $css
done