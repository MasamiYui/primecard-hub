// components/markdown/markdown.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    content: {
      type: String,
      value: ''
    },
    theme: {
      type: String,
      value: 'light'
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    parsedContent: ''
  },

  /**
   * 组件的方法列表
   */
  methods: {
    /**
     * 简单的 Markdown 解析
     * 支持基本的标题、段落、粗体、斜体等
     */
    parseMarkdown(content) {
      if (!content) return [];
      
      const nodes = [];
      const lines = content.split('\n');
      let currentParagraph = '';
      
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        
        if (!line) {
          // 空行，结束当前段落
          if (currentParagraph) {
            nodes.push(this.createParagraphNode(currentParagraph));
            currentParagraph = '';
          }
          continue;
        }
        
        // 处理标题
        if (line.startsWith('# ')) {
          if (currentParagraph) {
            nodes.push(this.createParagraphNode(currentParagraph));
            currentParagraph = '';
          }
          nodes.push(this.createHeadingNode(line.substring(2), 1));
        } else if (line.startsWith('## ')) {
          if (currentParagraph) {
            nodes.push(this.createParagraphNode(currentParagraph));
            currentParagraph = '';
          }
          nodes.push(this.createHeadingNode(line.substring(3), 2));
        } else if (line.startsWith('### ')) {
          if (currentParagraph) {
            nodes.push(this.createParagraphNode(currentParagraph));
            currentParagraph = '';
          }
          nodes.push(this.createHeadingNode(line.substring(4), 3));
        } else if (line.startsWith('![')) {
          // 处理图片（暂时忽略）
          continue;
        } else if (/^\d+\. /.test(line)) {
          // 处理有序列表
          if (currentParagraph) {
            nodes.push(this.createParagraphNode(currentParagraph));
            currentParagraph = '';
          }
          nodes.push(this.createListItemNode(line.replace(/^\d+\. /, ''), true));
        } else if (line.startsWith('- ') || line.startsWith('* ')) {
          // 处理无序列表
          if (currentParagraph) {
            nodes.push(this.createParagraphNode(currentParagraph));
            currentParagraph = '';
          }
          nodes.push(this.createListItemNode(line.substring(2), false));
        } else {
          // 普通文本行
          if (currentParagraph) {
            currentParagraph += '\n' + line;
          } else {
            currentParagraph = line;
          }
        }
      }
      
      // 处理最后的段落
      if (currentParagraph) {
        nodes.push(this.createParagraphNode(currentParagraph));
      }
      
      return nodes;
    },
    
    /**
     * 创建标题节点
     */
    createHeadingNode(text, level) {
      const fontSize = level === 1 ? '40rpx' : level === 2 ? '36rpx' : '32rpx';
      const margin = level === 1 ? '32rpx 0 24rpx 0' : level === 2 ? '28rpx 0 20rpx 0' : '24rpx 0 16rpx 0';
      
      return {
        name: 'div',
        attrs: {
          style: `font-size: ${fontSize}; font-weight: bold; margin: ${margin}; color: #2c3e50;`
        },
        children: [{
          type: 'text',
          text: text
        }]
      };
    },
    
    /**
      * 创建段落节点
      */
     createParagraphNode(text) {
       const children = this.parseInlineElements(text);
       
       return {
         name: 'div',
         attrs: {
           style: 'margin: 16rpx 0; text-align: justify; line-height: 1.6;'
         },
         children: children
       };
     },
     
     /**
      * 创建列表项节点
      */
     createListItemNode(text, isOrdered) {
       const children = this.parseInlineElements(text);
       
       return {
         name: 'div',
         attrs: {
           style: 'margin: 8rpx 0; padding-left: 32rpx; line-height: 1.6; position: relative;'
         },
         children: [
           {
             name: 'span',
             attrs: {
               style: 'position: absolute; left: 0; top: 0; color: #666;'
             },
             children: [{
               type: 'text',
               text: isOrdered ? '•' : '•'
             }]
           },
           {
             name: 'span',
             children: children
           }
         ]
       };
     },
    
    /**
     * 解析行内元素（粗体、斜体等）
     */
    parseInlineElements(text) {
      const children = [];
      let currentText = '';
      let i = 0;
      
      while (i < text.length) {
        if (text[i] === '*' && text[i + 1] === '*') {
          // 粗体
          if (currentText) {
            children.push({ type: 'text', text: currentText });
            currentText = '';
          }
          
          const endIndex = text.indexOf('**', i + 2);
          if (endIndex !== -1) {
            const boldText = text.substring(i + 2, endIndex);
            children.push({
              name: 'span',
              attrs: {
                style: 'font-weight: bold; color: #e74c3c;'
              },
              children: [{ type: 'text', text: boldText }]
            });
            i = endIndex + 2;
          } else {
            currentText += text[i];
            i++;
          }
        } else if (text[i] === '*') {
          // 斜体
          if (currentText) {
            children.push({ type: 'text', text: currentText });
            currentText = '';
          }
          
          const endIndex = text.indexOf('*', i + 1);
          if (endIndex !== -1) {
            const italicText = text.substring(i + 1, endIndex);
            children.push({
              name: 'span',
              attrs: {
                style: 'font-style: italic; color: #3498db;'
              },
              children: [{ type: 'text', text: italicText }]
            });
            i = endIndex + 1;
          } else {
            currentText += text[i];
            i++;
          }
        } else {
          currentText += text[i];
          i++;
        }
      }
      
      if (currentText) {
        children.push({ type: 'text', text: currentText });
      }
      
      return children;
    }
  },

  /**
   * 组件生命周期
   */
  lifetimes: {
    attached() {
      this.setData({
        parsedContent: this.parseMarkdown(this.data.content)
      });
    }
  },

  /**
   * 监听属性变化
   */
  observers: {
    'content': function(newContent) {
      this.setData({
        parsedContent: this.parseMarkdown(newContent)
      });
    }
  }
});