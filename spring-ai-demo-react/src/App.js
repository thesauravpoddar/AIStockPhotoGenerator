import React, { useState } from 'react';
import './App.css';
import ImageGenerator from './components/ImageGenerator';
import ChatComponent from './components/ChatComponent';
import RecipeGenerator from './components/RecipeGenerator';

const TOOLS = [
  { id: 'image-generator', label: 'Image Generator' },
  { id: 'chat', label: 'Ask AI' },
  { id: 'recipe-generator', label: 'Recipe Generator' },
];

function App() {
  const [activeTab, setActiveTab] = useState('image-generator');
  const activeIndex = TOOLS.findIndex((tool) => tool.id === activeTab);

  return (
      <div className="App">
        <header className="app-header">
        <span className="eyebrow">
          AI Workbench · 0{activeIndex + 1} / 0{TOOLS.length}
        </span>
          <h1>{TOOLS[activeIndex].label}</h1>
        </header>

        <div className="tool-switcher" data-accent={activeTab}>
          <div
              className="tool-switcher__indicator"
              style={{ transform: `translateX(${activeIndex * 100}%)` }}
          />
          <div className="tabs" style={{ display: 'contents' }}>
            {TOOLS.map((tool) => (
                <button
                    key={tool.id}
                    className={activeTab === tool.id ? 'active' : ''}
                    onClick={() => setActiveTab(tool.id)}
                >
                  {tool.label}
                </button>
            ))}
          </div>
        </div>

        <div className="tab-content">
          {activeTab === 'image-generator' && <ImageGenerator />}
          {activeTab === 'chat' && <ChatComponent />}
          {activeTab === 'recipe-generator' && <RecipeGenerator />}
        </div>
      </div>
  );
}

export default App;