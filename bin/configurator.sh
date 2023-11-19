input_file="application.properties.SAMPLE"
values_file="configurator.properties"

output_file=$(echo "$input_file" | sed 's/\.SAMPLE//')

awk -F= 'NR==FNR {a[$1]=$2; next} {while (match($0, /{{([^}]*)}}/)) { key=substr($0, RSTART+2, RLENGTH-4); if(key in a) $0=substr($0, 1, RSTART-1) a[key] substr($0, RSTART+RLENGTH); else $0=substr($0, 1, RSTART-1) "{{"key"}" substr($0, RSTART+RLENGTH) } } 1' "$values_file" "$input_file" > "$output_file"

