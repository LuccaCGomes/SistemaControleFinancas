find . -type f -name "*.java" | while read filePath; do
    if [ -f "$filePath" ]; then
        echo "Arquivo: $filePath" >> projeto.txt

        cat "$filePath" >> projeto.txt

        echo -e "\n" >> projeto.txt
    else
        echo "Arquivo não encontrado: $filePath"
    fi
done