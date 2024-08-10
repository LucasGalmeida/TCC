import React from 'react';


const ChatView: React.FC = () => {

  return (
    <p>
        Menu esquerdo vai ter duas opções
        Chat
        Documentos
        eles vão ser do tipo que expande
        
        no chat vai ter um titulo que pode ser renomeado
        também vai haver um + ao lado para criar um novo chat
        vai haver uma tabela histórico chat
        vou salvar o que o usuário perguntou e o que a ia respondeu
        colunas: id, texto, tipo (usuario, IA), data, usuarioReferencia
        ao entrar na tela, carregaremos todas as mensagens daquele chat 

        no documento ao clicar em + vai abrir um modal
        o modal vai ser possivel inserir um documento
        ao terminar o upload, perguntar se quer processar o documento
        se sim, chama ia, e troca status
        na tela de listagem, documentos processados ficam com v e não processados ficam com x



    </p>
    
  );
};

export default ChatView;
