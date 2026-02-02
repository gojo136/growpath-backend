-- Create tasks table in Supabase
CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    category VARCHAR(100),
    due_date DATE NOT NULL,
    due_time TIME,
    repeat_type VARCHAR(10) DEFAULT 'NONE',
    has_reminder BOOLEAN DEFAULT FALSE,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_tasks_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_priority CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
    CONSTRAINT chk_repeat_type CHECK (repeat_type IN ('NONE', 'DAILY', 'WEEKLY', 'MONTHLY')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'COMPLETED'))
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_user_due_date ON tasks(user_id, due_date);

-- Enable Row Level Security (RLS)
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;

-- Create RLS policies
CREATE POLICY "Users can view their own tasks" ON tasks
    FOR SELECT USING (user_id = auth.uid()::bigint);

CREATE POLICY "Users can insert their own tasks" ON tasks
    FOR INSERT WITH CHECK (user_id = auth.uid()::bigint);

CREATE POLICY "Users can update their own tasks" ON tasks
    FOR UPDATE USING (user_id = auth.uid()::bigint);

CREATE POLICY "Users can delete their own tasks" ON tasks
    FOR DELETE USING (user_id = auth.uid()::bigint);