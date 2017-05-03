# encoding: utf-8

require 'logstash-core'
require 'logstash/logging'
require 'logstash/environment'
require 'logstash/devutils/rspec/logstash_helpers'
require 'logstash/devutils/rspec/shared_examples'
require 'logstash/filters/useragent'
require 'logstash/filters/useragent_java'
require 'yaml'

testcases = [
  'additional_os_tests.yaml', 'firefox_user_agent_strings.yaml',
  'opera_mini_user_agent_strings.yaml', 'pgts_browser_list.yaml',
  'pgts_browser_list-orig.yaml', 'test_device.yaml', 'test_os.yaml',
  'test_ua.yaml'
]

test_case_path = File.expand_path('../../../../ua-parser/src/test/resources', __FILE__)

lines = testcases.flat_map do |test_case_file|
  YAML.load_file(File.join(test_case_path, test_case_file))['test_cases'].map do |test_case|
    test_case['user_agent_string']
  end
end

#
#::File.open('/tmp/lines.txt', 'a+') do |f|
#  lines.each do |line|
#    f.write "#{line}\n"
#  end
#  f.flush
#end

ruby_filter = LogStash::Filters::UserAgent.new('source' => 'foo')
ruby_filter.register
java_filter = LogStash::Filters::UserAgentJava.new('source' => 'foo')
java_filter.register

puts 'missing'
puts(Time.now.to_s)
(1..lines.length).each do |n|
  ruby_filter.lookup_useragent lines[n]
end

puts(Time.now.to_s)

(1..lines.length).each do |n|
  java_filter.lookup_useragent lines[n]
end

puts(Time.now.to_s)

puts 'hitting 10x'

puts(Time.now.to_s)
(1..lines.length).each do |n|
  (1..10).each do
    ruby_filter.lookup_useragent lines[n]
  end
end

puts(Time.now.to_s)

(1..lines.length).each do |n|
  (1..10).each do
    java_filter.lookup_useragent lines[n]
  end
end

puts(Time.now.to_s)
