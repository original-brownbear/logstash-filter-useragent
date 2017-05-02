require 'json'

BASE_PATH = File.expand_path(File.dirname(__FILE__))
@files = JSON.parse(File.read(File.join(BASE_PATH, 'vendor.json')))

task :default do
  system("rake -T")
end

require "logstash/devutils/rake"
require 'jars/installer'

task :install_jars do
  # If we don't have these env variables set, jar-dependencies will
  # download the jars and place it in $PWD/lib/. We actually want them in
  # $PWD/vendor
  ENV['JARS_HOME'] = Dir.pwd + "/vendor/jar-dependencies/runtime-jars"
  ENV['JARS_VENDOR'] = "false"
  `cd ua-parser && mvn clean install -DskipTests && cd ..`
  Jars::Installer.new.vendor_jars!(false, ENV['JARS_HOME'])
end
