find . -type f -name "*.java" | while read filePath; do
    if [ -f "$filePath" ]; then
        # Adiciona o nome do arquivo no início
        echo "Arquivo: $filePath" >> projeto.txt

        # Adiciona o conteúdo do arquivo
        cat "$filePath" >> projeto.txt

        # Adiciona uma linha separadora entre os conteúdos dos arquivos
        echo -e "\n" >> projeto.txt
    else
        echo "Arquivo não encontrado: $filePath"
    fi
done