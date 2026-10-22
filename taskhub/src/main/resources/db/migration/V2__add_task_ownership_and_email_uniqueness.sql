ALTER TABLE tasks ADD COLUMN IF NOT EXISTS user_id BIGINT;

CREATE INDEX IF NOT EXISTS ix_tasks_user_id ON tasks (user_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users (email);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_tasks_user'
    ) THEN
        ALTER TABLE tasks
            ADD CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id);
    END IF;
END $$;

CREATE SEQUENCE IF NOT EXISTS task_group_sequence START WITH 1 INCREMENT BY 50;
SELECT setval(
    'task_group_sequence',
    GREATEST(COALESCE((SELECT MAX(id) FROM task_group), 0) + 1, 1),
    FALSE
);
